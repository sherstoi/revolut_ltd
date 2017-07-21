# revolut_ltd
Testing code for Revolution Ltd company.

**How to run**
1. Move to root directory of this project.
2. Exceute *mvn clean install* command.
3. Run snapshot jar file in target directory:
   java -jar revolut_ltd-1.0-SNAPSHOT.jar
   **or** execute run method of AppStarter class in 
   Intellij Idea (or Eclipse).
4. Server should be available on port 8080.
5. Use postman or other tools to send request to server. 
Some examples of http requests:

- Create new account: POST http://localhost:8080/create 
   {
      "accountId":12345,
      "balance":56
   }
   
- Find all accounts:
GET http://localhost:8080/findall

- Find account by id:
GET http://localhost:8080/findacc?accId=12345

- Delete all accounts:
DELETE http://localhost:8080/deleteall

- Transfer money from one account to another:
POST http://localhost:8080/transfer?fromAccId=123456&toAccId=12345&balance=10
