  <h3 align="center">Dummy word game</h3>
  

  <details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
    <a href="#general-description">General description</a></li>
    <li><a href="#architecture">Architecture</a></li>
     <li><a href="#communication">Communication</a></li>
     <li><a href="#moduleApp">Module App</a></li>
    <li><a href="#moduleBot">Module Bot</a></li>
    <li><a href="#moduleCommon">Module Common</a></li>
    <li><a href="#tests">Tests</a></li>
     </ol>
</details>

<!-- ABOUT THE PROJECT -->
## General description

Dummy word game is a project that includes game with simple rule - player enters the word to start the game, there's bot who responds with entered word and adds new unique word , now its player's turn again. game continues until any rule will be broken. Project's purpose is to demonnstrate how to handle different arcchitectural challenges only and it is not for commercial or any other use!!!!!!!!!.

(actually do whatever you want with project) 

## Architecture
Project consists of 3 modules

<br> 1.App module - Main application module that is responsible for displaying UI and main game logic. 
<br> 2.Bot module - Bot application that is responsible for generating bot's responses and responding. 
<br> 3.Common module - Module that has commonn logic between bot and app modules

each module and its contents will be described in details bewlow.   

Dependencies between modules look like this  
<br>
![image](https://user-images.githubusercontent.com/25895125/122731049-f5240e00-d28b-11eb-97a3-1987f0c1155b.png)

Notice that project consists of two app modules which can be installed seperately on same device. 
Each app module has  "common" dependency. 


## Communication

Communnication between two app modules is done with Bound services model, which can be alternatively described as client-server communication in this case client  can be represented as App module and server - as Bot module. 

Use case diagram for communication is depicted in following picture

![image](https://user-images.githubusercontent.com/25895125/122736777-6d410280-d291-11eb-8841-53adea7d0529.png)

App module will  check if there is bot application installed on device and then tries to bind to its service. Service in bot application will start automatically after client  makes call bindService method. We will not start service explicitly as we want it to stop after client will unbind from service. 

As communication should be between two app processes we will use Messenger interface to do so. We will have Messenger object in both application and bot services. Messengers have Handlers in which we will respond to onMessageReceived callBack and read data in Message object in following manner 
```kotlin
 private val handlerCallback = Handler.Callback { msg ->
        when (msg.what) {
            MSG_WORD_RECEIVED_WHAT -> handleReceivedMessage(msg.data.getString(MSG_WORD_RECEIVED_KEY))
            MSG_WORD_SEND_WHAT -> handleSendMessage(msg)
        }
        true
    }
```
we will share than Ibinder from Messsenger object to client following way
```kotlin
 override fun onBind(intent: Intent?): IBinder? {
        messengerServer = Messenger(IncomingMessageHandler(handlerCallback))
        return messengerServer.binder
    }
```
to have two way communication between server and client we should define second messenger object in our service class which will be sent from client in message.replyTo parameter

Client code 
```kotlin
 try {
                val msg = Message.obtain(null, MSG_WORD_SEND_WHAT, 0, 0).apply {
                    replyTo = receiveMessenger
                }
                botService?.send(msg)
```


```kotlin
private val receiveMessageHandler = Handler.Callback {
        when (it.what) {
            MSG_WORD_SEND_WHAT -> viewModel.onWordReceivedIntent(it.data.getString(MSG_WORD_SENT_KEY)!!)
        }
        true
    }
    private val receiveMessenger = Messenger(MessageReceiverMessenger(receiveMessageHandler))
 ```
 
Server Code 
```
private fun handleSendMessage(message: Message) {
        messengerClient = message.replyTo
    }
```
Than we send messages from BotService with that messengerClient 

```kotlin
 messengerClient.send(Message.obtain(null, MSG_WORD_SEND_WHAT, 0, 0).apply {
                data = Bundle().apply {
                    putString(MSG_WORD_SENT_KEY, generateRandomWord(receivedWord))
                }
            })
            
````
I am using Bundle() to share and read data between app and bot applications. 

## Module App

This module represents main app module. Application is built with MVVM architecture , as a dependency injection tool i m using koin for simple and swifter implementation. 
ViewModel is responsible for delegating viewStates depending on business logic.

Architecture is depicted in following picture
![image](https://user-images.githubusercontent.com/25895125/122747283-f1988300-d29b-11eb-87f1-28c41e9087b6.png)

<br>
please note that 
Activity is delegated to exchange data with service. as application use case was small i did not think it was necessary to create separate data layer.
in production project data layer should be separate part of course. 

View model is communicating with Activity with Jetpack's LiveData 
on each viewState change i send new ViewState with only property that needs updating 
for example if viewModel deciedes that user lost the game only lostGame property will be updated in following way 
```kotlin
            sendState(mainViewState.copy(gameLost = DisposableValue(validationResult)))
``` 
Data Class 's .copy makes sure that old state will not be lost 
Disposable Value is Property wrapper class which has following behavior , once it is used it's value will be null, I do that to prevent redrawing states which weren't changed. 

Assuming that bot has (almost :)))  always "Right answer" I only validate user input with Word Validator classs . 

WordsValidator has main method doValidation which reeturns ValidationResult 
Validation result is a Sealed class which can only be one from following 

```kotlin
data class ValidationResultOK(val word:String):ValidationResult()

data class  ValidationResultLost(val lostReasons: LostReasons):ValidationResult()
```
if doValidation returns ValidationResultLost we have ability to determine what was wrong with user's input from lostReason property

LostReason itself is a sealed class which is parent for all possible Losing reasons 

```kotlin
sealed class LostReasons()
object LostReasonTwoWordsAdded:LostReasons()
object LostReasonNoWordAdded:LostReasons()
data class LostReasonWordRepeated(val repeatedWord:String):LostReasons()
data class LostReasonIncorrectWord(val incorrectWord:Map<String,String>):LostReasons()
```

As for visualization of game history I used heteroogenous recyclerView , it is achieved by returning different Int for bot and user's responsess in Adapters getItemViewType method

```kotlin
override fun getItemViewType(position: Int): Int {
        return currentList[position].playerType.ordinal
    }
```
Than in onCreateViewHolder i return different ViewHolders Both which are Children of BaseViewHolder abstract class

```kotlin
abstract class BaseViewHolder(binding: ViewBinding):RecyclerView.ViewHolder(binding.root) {
   abstract fun bind(itemDataModel:GameHistoryItemDataModel)
}
```
In each classsess implementation of bind method i do the binding of data and view. 
```kotlin
  inner class GameHistoryBotViewHolder(private val viewBinding: LayoutGameHistoryBotItemBinding)
        : BaseViewHolder(viewBinding) {
        override fun bind(itemDataModel: GameHistoryItemDataModel) {
            viewBinding.tvInput.text = itemDataModel.currentWord
            viewBinding.ivPlayerType.setImageDrawable(ContextCompat.getDrawable(viewBinding.root.context, R.drawable.ic_robot))
        }
    }
```
Project 100% uses Jetpack's viewBinding to work with the views.

I use DiffUtils class to update recycler view effectively

Koin is used to demonstrate how we can make dependecy injection easily and effectively. it only injects WordsValidator to ViewModel class


## Module Bot

Application com.adjarabet.bot consists solely of class DummyGameBot which extendss Android Service class. Application does not have launcher or activity so android studio will show error while launching app
![image](https://user-images.githubusercontent.com/25895125/122750923-30303c80-d2a0-11eb-87cf-b48191322486.png)

It is 100 % ok, as Applicatioin's purpose is to serve Client app and does not need any UI
Most part of this application is depicted in <a href="#communication">Communication</a> part. it receives and sends data to client. 

Application's other responsibility is to generate random word which will be sent to user later

I am using org.apache.commons.RandomTextUtils to generate random string because.... you know... why not? 

As game rules say that neither player can repeat words that was mentioned in game I use recursive function to generate unique word everytime client asks for it 

```kotlin
    private fun generateRandomWord(word: String?): String {
        return if (losingProbabiltyRange.contains(Random.nextInt(PERCENT_MAX_VALUE))) {
            BOT_LOSING_ERR_MESSAGE
        } else {
            val newWordList = word?.split(' ')
            val randWord = RandomStringUtils.randomAlphabetic(Random.nextInt(RANDOM_WORD_MAX_LENGTH))
            if (newWordList?.contains(randWord) == true ) {
                generateRandomWord(word)
            }
            "$word $randWord"
        }

    }
```
Here i also let user win with given probability its value can be change in constant 
```kotlin
        const val BOT_LOSING_PROBABILITY_PERCENT = 3
```
I also have logic to delay response with 500 ms because communication lookss smoother that way ^^


## Module Common

This module has purpose to share things between app and bot module , for given moment I only share constants which are necessarry for communication for both parties

```
object UserBotCommonLogicConstants {
    const val MSG_WORD_RECEIVED_WHAT = 31
    const val MSG_WORD_RECEIVED_KEY = "MSG_WORD_RECEIVED_KEY"
    const val MSG_WORD_SEND_WHAT = 3
    const val MSG_WORD_SENT_KEY = "MSG_WORD_SENT_KEY"
    const val BOT_LOSING_ERR_MESSAGE = "TOO_MUCH_FOR_ME"
}
```
In perspective that module can contain validation or other logic that should be shared across apps. 

## Tests 
I have written simple unit tests with Junit framework for demonstration purposes only 
tests ccan be found in following file src/androidTest/java/com/adjarabet/user/WordsValidatorTest.kt

Happy coding !!! 

https://media.giphy.com/media/Od0QRnzwRBYmDU3eEO/giphy.gif
