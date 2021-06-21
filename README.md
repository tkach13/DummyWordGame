  <h3 align="center">Dummy word game</h3>
  

  <details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
    <a href="#general-description">General description</a></li>
    <li><a href="#architecture">Architecture</a></li>
     <li><a href="#communication">Communication</a></li>
    <li><a href="#moduleBot">Module Bot</a></li>
    <li><a href="#moduleApp">Module App</a></li>
    </ol>
</details>

<!-- ABOUT THE PROJECT -->
## General description

Dummy word game is a project that includes game which rules are simple - player enters the word to start the game, theres bot who responds with entered word and adds new unique word , now its player's turn again. game continues until any rule will be broken. Project's purpose is to demonnstrate how to handle different arcchitectural challenges onnly and it is not for commercial or any other use  

## Architecture
Project consists of 3 modules

<br> 1.App module - Main application module that is responsible for displaying UI and main game logic. 
<br>   2.Bot module - Bot application that is responsible for generating bot's responses and responding. 
<br>  3.Common module - Module that has commonn logic between bot and app modules
  

Dependencies between modules look like this  
![image](https://user-images.githubusercontent.com/25895125/122731049-f5240e00-d28b-11eb-97a3-1987f0c1155b.png)


