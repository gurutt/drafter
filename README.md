# Drafter


[![Total alerts](https://img.shields.io/lgtm/alerts/g/gurutt/drafter.svg?logo=lgtm&logoWidth=18&style=popout)](https://lgtm.com/projects/g/gurutt/drafter/alerts/)        [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/gurutt/drafter.svg?logo=lgtm&logoWidth=18&style=popout)](https://lgtm.com/projects/g/gurutt/drafter/context:java)       

![Test Coverage: Java](https://img.shields.io/codecov/c/github/gurutt/drafter.svg?style=popout)  ![Build Status: Java](https://img.shields.io/travis/com/gurutt/drafter.svg?style=popout)


## Draftify Bot

Building the teams can be triggered via Telegram Bot.


#### Available Commands

###### List Players

`\players <sport type>`

Pulls available players for given sport.

###### Build teams

`\draft <players> | <sport type> | <player attribute> | <teams count>`

Creates balanced based on provided params


#### Attributes

| Name                  | Possible values | Description            |
|:-------------------:  |:--------:| :----------------------|
| players           | Any (comma separated) | Available players stored in DB. |
| sport type            | basketball, football | Available sport type. |
| player attribute       | Skill, Stamina | Player characteristic that can be used to balance the teams |
| teams count        | Any number | Number of teams that should be created |
