
package com.mycompany.chatapp;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

class Login {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }
    public String getFirstName() { return firstName;}
    public String getLastName() { return lastName;}
    public String getUsername() { return username;}
    public String getCellPhoneNumber() { return cellPhoneNumber;}

    public boolean checkUserName() {
        if (username.contains("_") && username.length() <= 5) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPasswordComplexity() {
        if (password.length() < 8) {
            return false;
        }
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) { hasCapital = true; }
            if (Character.isDigit(c)) { hasNumber = true; }
            if (!Character.isLetterOrDigit(c)) { hasSpecial = true; }
        }
        if (hasCapital && hasNumber && hasSpecial) {
            return true;
        } else {
            return false;
        }
    }
    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber.matches("^\\+[0-9]{10,12}$")) {
            return true;
        } else {
            return false;
        }
    }
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        if (enteredUsername.equals(username) && enteredPassword.equals(password)) {
            return true;
        } else {
            return false;
        }
    }
    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        if (loginUser(enteredUsername, enteredPassword)) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}

public class Message {

    private String messageID;
    private int numMessagesSent;
    private String recipient;
    private String message;
    private String messageHash;

    //arrays for Part 3
    static ArrayList<Message> sentMessages = new ArrayList<Message>();
    static ArrayList<Message> disregardedMessages = new ArrayList<Message>();
    static ArrayList<Message> storedMessages = new ArrayList<Message>();
    static ArrayList<String> messageHashes = new ArrayList<String>();
    static ArrayList<String> messageIDs = new ArrayList<String>();

    static int totalMessagesSent = 0;

    public Message(String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
        this.numMessagesSent = totalMessagesSent + 1;

        //generate random message ID using two random numbers joined together
        Random rand = new Random();
        int num1 = rand.nextInt(90000) + 10000;
        int num2 = rand.nextInt(90000) + 10000;
        this.messageID = num1 + "" + num2;

        this.messageHash = createMessageHash();
    }

    public boolean checkMessageID() {
        if (messageID.length() <= 10) {
            return true;
        } else {
            return false;
        }
    }

    //Regex to format a local SA number to international, const internationalNumber = localNumber.replace(/^0/, '+27');
    public String checkRecipientCell() {
        if (recipient.startsWith("+") && recipient.length() <= 13) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public String createMessageHash() {
        //get the first 2 numbers of the messageID
        String first2 = messageID.substring(0, 2);

        //split message by spaces to get words
        String[] words = message.split(" ");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        //remove the ? or . at the end of last word
        String cleanWord = "";
        int x = 0;
        while (x < lastWord.length()) {
            char letter = lastWord.charAt(x);
            if (Character.isLetterOrDigit(letter)) {
                cleanWord = cleanWord + letter;
            }
            x++;
        }
        
        String hash = first2 + ":" + numMessagesSent + ":" + firstWord + cleanWord;
        hash = hash.toUpperCase();

        return hash;
    }

    public String SentMessage(int choice) {
        if (choice == 1) {
            totalMessagesSent++;
            numMessagesSent = totalMessagesSent;
            sentMessages.add(this);
            messageHashes.add(this.messageHash);
            messageIDs.add(this.messageID);
            return "Message successfully sent.";
        } else if (choice == 2) {
            disregardedMessages.add(this);
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            storedMessages.add(this);
            messageHashes.add(this.messageHash);
            messageIDs.add(this.messageID);
            return "Message successfully stored.";
        } else {
            return "Invalid choice.";
        }
    }

    public static String printMessages() {
        if (sentMessages.size() == 0) {
            return "No messages sent yet.";
        }

        //Usage of for loop to go through all sent messages
        String result = "";
        for (int i = 0; i < sentMessages.size(); i++) {
            Message m = sentMessages.get(i);
            result = result + "Message ID: " + m.messageID + "\n";
            result = result + "Message Hash: " + m.messageHash + "\n";
            result = result + "Recipient: " + m.recipient + "\n";
            result = result + "Message: " + m.message + "\n";
        }
        return result;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public String storeMessage() {
        //save the message to a json file
        String json = "{\"messageID\":\"" + messageID + "\","
                + "\"numMessagesSent\":" + numMessagesSent + ","
                + "\"recipient\":\"" + recipient + "\","
                + "\"message\":\"" + message + "\","
                + "\"messageHash\":\"" + messageHash + "\"}";
        try {
            FileWriter fw = new FileWriter("messages.json", true);
            fw.write(json + "\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Could not save to file.");
        }
        return "Message successfully stored.";
    }

    public String validateMessage() {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = message.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getMessageHash() { return messageHash; }
    public int getNumMessagesSent() { return numMessagesSent; }

    // Put stored messages from JSON file into the storedMessages array
    public static void loadStoredMessages() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("messages.json"));
            String line = reader.readLine();
            while (line != null) {
                //parse each line manually
                String id = getJsonValue(line, "messageID");
                String recip = getJsonValue(line, "recipient");
                String msg = getJsonValue(line, "message");
                String hash = getJsonValue(line, "messageHash");

                if (id != null && recip != null && msg != null) {
                    Message m = new Message(recip, msg);
                    storedMessages.add(m);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("No stored messages file found.");
        }
    }

    //helper to get a value from a json line by key
    private static String getJsonValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) {
            return null;
        }
        start = start + search.length();
        int end = json.indexOf("\"", start);
        if (end == -1) {
            return null;
        }
        return json.substring(start, end);
    }

    //Display sender and recipient of all stored messages
    public static void displayStoredSenderRecipient() {
        if (storedMessages.size() == 0) {
            System.out.println("No stored messages.");
            return;
        }
        System.out.println("  Stored Messages: Sender and Recipient  ");
        int i = 0;
        while (i < storedMessages.size()) {
            Message m = storedMessages.get(i);
            System.out.println("Recipient: " + m.recipient);
            System.out.println("Message: " + m.message);
            i++;
        }
    }

    //display the longest stored message
    public static void displayLongestMessage() {

        ArrayList<Message> all = new ArrayList<Message>();
        int i = 0;
        while (i < sentMessages.size()) {
            all.add(sentMessages.get(i));
            i++;
        }
        i = 0;
        while (i < storedMessages.size()) {
            all.add(storedMessages.get(i));
            i++;
        }

        if (all.size() == 0) {
            System.out.println("No messages found.");
            return;
        }

        Message longest = all.get(0);
        int j = 1;
        while (j < all.size()) {
            if (all.get(j).message.length() > longest.message.length()) {
                longest = all.get(j);
            }
            j++;
        }
        System.out.println("  Longest Message  ");
        System.out.println("Recipient: " + longest.recipient);
        System.out.println("Message: " + longest.message);
        System.out.println("Length: " + longest.message.length() + " characters");
    }

    //search for a message by ID and display recipient and message
    public static void searchByMessageID(String searchID) {
        ArrayList<Message> all = new ArrayList<Message>();
        int i = 0;
        while (i < sentMessages.size()) {
            all.add(sentMessages.get(i));
            i++;
        }
        i = 0;
        while (i < storedMessages.size()) {
            all.add(storedMessages.get(i));
            i++;
        }

        boolean found = false;
        int j = 0;
        while (j < all.size()) {
            Message m = all.get(j);
            if (m.messageID.equals(searchID)) {
                System.out.println("  Message Found  ");
                System.out.println("Recipient: " + m.recipient);
                System.out.println("Message: " + m.message);
                found = true;
            }
            j++;
        }
        if (found == false) {
            System.out.println("Message ID not found.");
        }
    }

    //search all messages for a particular recipient
    public static void searchByRecipient(String searchRecipient) {
        ArrayList<Message> all = new ArrayList<Message>();
        int i = 0;
        while (i < sentMessages.size()) {
            all.add(sentMessages.get(i));
            i++;
        }
        i = 0;
        while (i < storedMessages.size()) {
            all.add(storedMessages.get(i));
            i++;
        }

        boolean found = false;
        int j = 0;
        while (j < all.size()) {
            Message m = all.get(j);
            if (m.recipient.equals(searchRecipient)) {
                System.out.println("Message: " + m.message);
                found = true;
            }
            j++;
        }
        if (found == false) {
            System.out.println("No messages found for that recipient.");
        }
    }

    //delete a message using its hash
    public static void deleteByHash(String hash) {
        //check sent messages
        int i = 0;
        while (i < sentMessages.size()) {
            if (sentMessages.get(i).messageHash.equals(hash)) {
                String deletedMsg = sentMessages.get(i).message;
                sentMessages.remove(i);
                messageHashes.remove(hash);
                System.out.println("Message: \"" + deletedMsg + "\" successfully deleted.");
                return;
            }
            i++;
        }
        //check stored messages
        int j = 0;
        while (j < storedMessages.size()) {
            if (storedMessages.get(j).messageHash.equals(hash)) {
                String deletedMsg = storedMessages.get(j).message;
                storedMessages.remove(j);
                messageHashes.remove(hash);
                System.out.println("Message: \"" + deletedMsg + "\" successfully deleted.");
                return;
            }
            j++;
        }
        System.out.println("Message hash not found.");
    }

    //display full report of all stored messages
    public static void displayReport() {
        ArrayList<Message> all = new ArrayList<Message>();
        int i = 0;
        while (i < sentMessages.size()) {
            all.add(sentMessages.get(i));
            i++;
        }
        i = 0;
        while (i < storedMessages.size()) {
            all.add(storedMessages.get(i));
            i++;
        }

        if (all.size() == 0) {
            System.out.println("No messages to display.");
            return;
        }

        System.out.println("  Full Report of All Messages  ");
        int j = 0;
        while (j < all.size()) {
            Message m = all.get(j);
            System.out.println("Message Hash: " + m.messageHash);
            System.out.println("Recipient: " + m.recipient);
            System.out.println("Message: " + m.message);
            j++;
        }
    }

    //stored messages sub menu
    public static void storedMessagesMenu(Scanner input) {
        boolean back = false;
        while (back == false) {
            System.out.println("  Stored Messages Menu  ");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest message");
            System.out.println("3) Search for a message by ID");
            System.out.println("4) Search messages by recipient");
            System.out.println("5) Delete a message using message hash");
            System.out.println("6) Display full report");
            System.out.println("7) Back to main menu");
            System.out.print("Choose: ");
            String pick = input.nextLine();

            if (pick.equals("1")) {
                displayStoredSenderRecipient();
            } else if (pick.equals("2")) {
                displayLongestMessage();
            } else if (pick.equals("3")) {
                System.out.print("Enter Message ID to search: ");
                String searchID = input.nextLine();
                searchByMessageID(searchID);
            } else if (pick.equals("4")) {
                System.out.print("Enter recipient number to search: ");
                String searchRecip = input.nextLine();
                searchByRecipient(searchRecip);
            } else if (pick.equals("5")) {
                System.out.print("Enter message hash to delete: ");
                String hashToDelete = input.nextLine();
                deleteByHash(hashToDelete);
            } else if (pick.equals("6")) {
                displayReport();
            } else if (pick.equals("7")) {
                back = true;
            } else {
                System.out.println("Please enter 1 to 7.");
            }
        }
    }

    public static void sendMessage(Scanner input) {

        //keep asking for a number until they give a valid one
        String cellNum = "";
        boolean numOk = false;
        while (numOk == false) {
            System.out.print("Enter recipient cell number (With International Code +27): ");
            cellNum = input.nextLine();

            Message temp = new Message(cellNum, "test");
            String check = temp.checkRecipientCell();
            System.out.println(check);

            if (check.equals("Cell phone number successfully captured.")) {
                numOk = true;
            }
        }

        //keep asking for a message until its under 250 chars
        String msg = "";
        boolean msgOk = false;
        while (msgOk == false) {
            System.out.print("Enter your message (max 250 characters): ");
            msg = input.nextLine();

            if (msg.length() > 250) {
                int over = msg.length() - 250;
                System.out.println("Please enter a message of less than 250 characters.");
                System.out.println("Your message is " + over + " characters too long.");
            } else {
                msgOk = true;
            }
        }

        System.out.println("Message successfully sent");

        Message newMsg = new Message(cellNum, msg);

        System.out.println("Message ID: " + newMsg.getMessageID());
        System.out.println("Message Hash: " + newMsg.getMessageHash());
        System.out.println("Recipient: " + newMsg.getRecipient());
        System.out.println("Message: " + newMsg.getMessage());

        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Choose: ");
        int pick = Integer.parseInt(input.nextLine());

        String res = newMsg.SentMessage(pick);

        if (pick == 3) {
            newMsg.storeMessage();
        }

        System.out.println(res);
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        //load any previously stored messages from json file
        loadStoredMessages();

        //REGISTRATION
        System.out.println(" REGISTER ");

        System.out.print("Enter First Name: ");
        String fName = input.nextLine();

        System.out.print("Enter Last Name: ");
        String lName = input.nextLine();

        System.out.print("Enter Username (must have _ and max 5 chars: ");
        String user = input.nextLine();
        Login tempUsername = new Login(fName, lName, user, "", "");
        if (tempUsername.checkUserName()) {
            System.out.println("Username successfully captured.");
        } else {
            System.out.println("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.");
        }

        System.out.print("Enter Password (8+ chars, 1 capital, 1 number, 1 special: ");
        String pass = input.nextLine();
        Login tempPassword = new Login(fName, lName, user, pass, "");
        if (tempPassword.checkPasswordComplexity()) {
            System.out.println("Password successfully captured.");
        } else {
            System.out.println("Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number and a special character.");
        }

        System.out.print("Enter CellPhone Number (With international code +27): ");
        String phone = input.nextLine();
        Login registeredUser = new Login(fName, lName, user, pass, phone);
        if (registeredUser.checkCellPhoneNumber()) {
            System.out.println("Cellphone number successfully added.");
        } else {
            System.out.println("Cellphone number incorrectly formatted or does not contain international code.");
        }

        //LOGIN
        System.out.println(" LOGIN ");

        System.out.print("Enter First Name: ");
        String enteredFirstName = input.nextLine();

        if (enteredFirstName.equals(registeredUser.getFirstName())) {
            System.out.println("First name Match.");
        } else {
            System.out.println("First name does not match.");
        }

        System.out.print("Enter Last Name: ");
        String enteredLastName = input.nextLine();

        if (enteredLastName.equals(registeredUser.getLastName())) {
            System.out.println("Last name Match.");
        } else {
            System.out.println("Last name does not match.");
        }

        System.out.print("Enter Username: ");
        String enteredUser = input.nextLine();

        if (enteredUser.equals(registeredUser.getUsername())) {
            System.out.println("Username Match.");
        } else {
            System.out.println("Username does not match.");
        }

        System.out.print("Enter Password: ");
        String enteredPass = input.nextLine();

        if (registeredUser.loginUser(enteredUser, enteredPass)) {
            System.out.println("Password Match.");
        } else {
            System.out.println("Password does not match.");
        }

        System.out.print("Enter CellPhone Number (With International Code +27): ");
        String enteredPhone = input.nextLine();

        if (enteredPhone.equals(registeredUser.getCellPhoneNumber())) {
            System.out.println("CellPhone number Match.");
        } else {
            System.out.println("CellPhone number does not match.");
        }

        //LOGIN STATUS
        System.out.println(" LOGIN STATUS ");

        boolean namesMatch = false;
        boolean usernameMatch = false;
        boolean passwordMatch = false;
        boolean cellphoneMatch = false;

        if (enteredFirstName.equals(registeredUser.getFirstName()) && enteredLastName.equals(registeredUser.getLastName())) {
            namesMatch = true;
        }
        if (enteredUser.equals(registeredUser.getUsername())) {
            usernameMatch = true;
        }
        if (registeredUser.loginUser(enteredUser, enteredPass)) {
            passwordMatch = true;
        }
        if (enteredPhone.equals(registeredUser.getCellPhoneNumber())) {
            cellphoneMatch = true;
        }
        if (namesMatch && usernameMatch && passwordMatch && cellphoneMatch) {
        } else {
            System.out.println("Username or password incorrect, please try again.");
            input.close();
            return;
        }

        //QUICKCHAT
        System.out.println("Welcome to QuickChat.");

        System.out.print("How many messages would you like to send? ");
        int numMessages = Integer.parseInt(input.nextLine());

        int count = 0;
        boolean running = true;

        while (running == true) {
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");
            String choice = input.nextLine();

            if (choice.equals("1")) {
                if (count >= numMessages) {
                    System.out.println("You have reached your message limit.");
                } else {
                    sendMessage(input);
                    count++;
                }
            } else if (choice.equals("2")) {
                System.out.println(printMessages());
            } else if (choice.equals("3")) {
                storedMessagesMenu(input);
            } else if (choice.equals("4")) {
                running = false;
            } else {
                System.out.println("Please enter 1, 2, 3 or 4.");
            }
        }

        System.out.println("Total messages sent: " + returnTotalMessages());
        System.out.println(printMessages());
        System.out.println("Goodbye!");

        input.close();
    }
}
