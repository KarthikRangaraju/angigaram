# Angigaram

![angigaram](angigaram.jpg)


A slack application to recognize your colleagues in/across slack channel(s)! 
Angigaram means recognition in Tamil.

- Invite the angigaram slack bot to your channel. 
- Configure the app to capture a set of emojis a.k.a badges. For example: coin, burrito (could be your own custom emojis too)
- Whenever someone is mentioned in a message with the angigaram supported badges, the app records it in it's database. Example:
  `Hey @slackuser thanks for helping me with the customer issue!! Here's some coins for you: :coin :coin`
  the @slackuser would earn 2 coins in the app.
- Users can only give a configured amount of supported badges for the day. IE, If you configured 
  total badges to be 5, A user can only give max 5 badges for the day.
- One a weekly/monthly basis the `@slackuser` would get a reminder of all the coins/burrito
  he/she has earned for the week to make them feel recognized. Note that recognition is purposefully
  made personal instead of being competitive(like a leaderboard).
- With the data collected, the company's HR could determine and reward "Employee of the month/quarter" and so on. 


##### Running the app locally:

```
export ANGIGARAM_SLACK_BOT_OAUTH_TOKEN='<Get it from slack>'; export ANGIGARAM_SUPPORTED_BADGES='coin, burrito'; export ANGIGARAM_TOTAL_DAILY_BADGES='5'
docker-compose up mysql web
```


##### Credits:
Angigaram characterization: Srikanta Umakantha