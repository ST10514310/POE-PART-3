package com.mycompany.chatapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class MessageTest {

    @BeforeEach
    public void setUp() {
        Message.sentMessages.clear();
        Message.disregardedMessages.clear();
        Message.storedMessages.clear();
        Message.messageHashes.clear();
        Message.messageIDs.clear();
        Message.totalMessagesSent = 0;
    }

    //test 1 sent messages array is correctly populated with test data
    @Test
    public void testSentMessagesArrayPopulated() {
        Message msg1 = new Message("+27608294678", "Hey, Call me when your free.");
        msg1.SentMessage(1);

        Message msg2 = new Message("+27608294678", "Hey, how are you");
        msg2.SentMessage(1);

        assertEquals(2, Message.sentMessages.size());
        assertEquals("Hey, Call me when your free.", Message.sentMessages.get(0).getMessage());
        assertEquals("Hey, how are you", Message.sentMessages.get(1).getMessage());
    }

    //test 2 display the longest message
    @Test
    public void testLongestMessage() {
        Message msg1 = new Message("+27608294678", "Hey, Call me when your free.");
        msg1.SentMessage(1);

        Message msg2 = new Message("+27608294678", "Hey, how are you");
        msg2.SentMessage(1);

        Message msg3 = new Message("+27608294678", "Hello, Are you good ?");
        msg3.SentMessage(3);

        Message msg4 = new Message("+27608294678", "Hello, How are you.");
        msg4.SentMessage(3);

        ArrayList<Message> all = new ArrayList<Message>();
        for (int i = 0; i < Message.sentMessages.size(); i++) {
            all.add(Message.sentMessages.get(i));
        }
        for (int i = 0; i < Message.storedMessages.size(); i++) {
            all.add(Message.storedMessages.get(i));
        }

        Message longest = all.get(0);
        for (int i = 1; i < all.size(); i++) {
            if (all.get(i).getMessage().length() > longest.getMessage().length()) {
                longest = all.get(i);
            }
        }
        assertEquals("Hey, Call me when your free.", longest.getMessage());
    }

    //test 3 search for a message by ID returns correct message
    @Test
    public void testSearchByMessageID() {
        Message msg1 = new Message("+27608294678", "Hey, Call me when your free.");
        msg1.SentMessage(1);

        String searchID = msg1.getMessageID();
        boolean found = false;

        ArrayList<Message> all = new ArrayList<Message>();
        for (int i = 0; i < Message.sentMessages.size(); i++) {
            all.add(Message.sentMessages.get(i));
        }
        for (int i = 0; i < Message.storedMessages.size(); i++) {
            all.add(Message.storedMessages.get(i));
        }

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getMessageID().equals(searchID)) {
                found = true;
                assertEquals("Hey, Call me when your free.", all.get(i).getMessage());
            }
        }
        assertTrue(found);
    }

    //test 4 search all messages for a particular recipient
    @Test
    public void testSearchByRecipient() {
        Message msg1 = new Message("+27608294678", "Hey, Call me when your free.");
        msg1.SentMessage(1);

        Message msg2 = new Message("+27608294678", "Hello, Are you good ?");
        msg2.SentMessage(3);

        String searchRecipient = "+27608294678";
        ArrayList<String> results = new ArrayList<String>();

        ArrayList<Message> all = new ArrayList<Message>();
        for (int i = 0; i < Message.sentMessages.size(); i++) {
            all.add(Message.sentMessages.get(i));
        }
        for (int i = 0; i < Message.storedMessages.size(); i++) {
            all.add(Message.storedMessages.get(i));
        }

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getRecipient().equals(searchRecipient)) {
                results.add(all.get(i).getMessage());
            }
        }

        assertEquals(2, results.size());
        assertTrue(results.contains("Hey, Call me when your free."));
        assertTrue(results.contains("Hello, Are you good ?"));
    }

    //test 5 delete a message using its message hash
    @Test
    public void testDeleteByHash() {
        Message msg1 = new Message("+27608294678", "Hey, Call me when your free.");
        msg1.SentMessage(3);

        Message msg2 = new Message("+27608294678", "Hello, Are you good ?");
        msg2.SentMessage(3);

        String hashToDelete = msg1.getMessageHash();
        int sizeBefore = Message.storedMessages.size();

        for (int i = 0; i < Message.storedMessages.size(); i++) {
            if (Message.storedMessages.get(i).getMessageHash().equals(hashToDelete)) {
                Message.storedMessages.remove(i);
                break;
            }
        }

        assertEquals(sizeBefore - 1, Message.storedMessages.size());
    }

    //test 6 display report shows all messages with hash recipient and message
    @Test
    public void testDisplayReport() {
        Message msg1 = new Message("+27608294678", "Hey, Call me when your free.");
        msg1.SentMessage(1);

        Message msg2 = new Message("+27608294678", "Hey, how are you");
        msg2.SentMessage(1);

        Message msg3 = new Message("+27608294678", "Hello, Are you good ?");
        msg3.SentMessage(3);

        Message msg4 = new Message("+27608294678", "Hello, How are you.");
        msg4.SentMessage(3);

        ArrayList<Message> all = new ArrayList<Message>();
        for (int i = 0; i < Message.sentMessages.size(); i++) {
            all.add(Message.sentMessages.get(i));
        }
        for (int i = 0; i < Message.storedMessages.size(); i++) {
            all.add(Message.storedMessages.get(i));
        }

        assertEquals(4, all.size());

        //check every message has a hash, recipient and message
        for (int i = 0; i < all.size(); i++) {
            assertNotNull(all.get(i).getMessageHash());
            assertNotNull(all.get(i).getRecipient());
            assertNotNull(all.get(i).getMessage());
        }
    }

    //test 7 message under 250 chars passes validation
    @Test
    public void testMessageUnder250() {
        Message msg = new Message("+27608294678", "Hey, Call me when your free.");
        assertEquals("Message ready to send.", msg.validateMessage());
    }

    //test 8 message over 250 chars fails validation
    @Test
    public void testMessageOver250() {
        String longMsg = "";
        for (int i = 0; i < 260; i++) {
            longMsg = longMsg + "a";
        }
        Message msg = new Message("+27608294678", longMsg);
        assertTrue(msg.validateMessage().startsWith("Message exceeds 250 characters by"));
    }

    //test 9 valid cell number is accepted
    @Test
    public void testValidCellNumber() {
        Message msg = new Message("+27608294678", "Hey, Call me when your free.");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    //test 10 number without international code is rejected
    @Test
    public void testNoInternationalCode() {
        Message msg = new Message("0608294678", "Hey, how are you");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", msg.checkRecipientCell());
    }

    //test 11 message hash has 3 parts
    @Test
    public void testHashHas3Parts() {
        Message msg = new Message("+27608294678", "Hey, Call me when your free.");
        String[] parts = msg.getMessageHash().split(":");
        assertEquals(3, parts.length);
    }

    //test 12 message hash last part is HEY,FREE
    @Test
    public void testHashLastPart() {
        Message msg = new Message("+27608294678", "Hey, Call me when your free.");
        String[] parts = msg.getMessageHash().split(":");
        assertEquals("HEY,FREE", parts[2]);
    }

    //test 13 message ID is exactly 10 digits
    @Test
    public void testMessageIDLength() {
        Message msg = new Message("+27608294678", "Hey, Call me when your free.");
        assertEquals(10, msg.getMessageID().length());
        assertTrue(msg.checkMessageID());
    }

    //test 14 send option returns correct message
    @Test
    public void testSendOption() {
        Message msg = new Message("+27608294678", "Hey, Call me when your free.");
        assertEquals("Message successfully sent.", msg.SentMessage(1));
    }

    //test 15 disregard option returns correct message
    @Test
    public void testDisregardOption() {
        Message msg = new Message("+27608294678", "Hello, Are you good ?");
        assertEquals("Press 0 to delete the message.", msg.SentMessage(2));
    }

    //test 16 store option returns correct message
    @Test
    public void testStoreOption() {
        Message msg = new Message("+27608294678", "Hello, How are you.");
        assertEquals("Message successfully stored.", msg.SentMessage(3));
    }
}
