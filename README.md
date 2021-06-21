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
     </ol>
</details>

<!-- ABOUT THE PROJECT -->
## General description

Dummy word game is a project that includes game which rules are simple - player enters the word to start the game, theres bot who responds with entered word and adds new unique word , now its player's turn again. game continues until any rule will be broken. Project's purpose is to demonnstrate how to handle different arcchitectural challenges onnly and it is not for commercial or any other use  

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

Communnication between two app modules is done with Bound services model, which can be alternatively described as client-server communication in this case client is can be interpreteed as App module and server - as Bot module. 

Use case diagram for communication is depicted in following picture

![image](https://user-images.githubusercontent.com/25895125/122736777-6d410280-d291-11eb-8841-53adea7d0529.png)

