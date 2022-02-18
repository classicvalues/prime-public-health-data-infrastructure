package gov.cdc.prime.phdi.utilities

import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.junit.jupiter.api.Test

class HelperFunctonTest {
    val singleMessage: String = javaClass.getResource("/singleMessage.hl7").readText()
    val multipleMessages: String = javaClass.getResource("/multipleMessages.hl7").readText()
    val mixedMessages: String = javaClass.getResource("/mixedMessages.hl7").readText()
    val batchFileSingleMessage: String = javaClass.getResource("/batchFileSingleMessage.hl7").readText()
    val batchFileMultipleMessages: String = javaClass.getResource("/batchFileMultipleMessages.hl7").readText()
    val batchFileNoMessage: String = javaClass.getResource("/batchFileNoMessage.hl7").readText()
    val singleInvalidMessage: String = javaClass.getResource("/singleInvalidMessage.hl7").readText()
    val loremIpsum: String = javaClass.getResource("/loremIpsum.txt").readText()

    // TEST READING FUNCTIONALITY
    @Test
    fun testReadHL7MessagesFromByteArray() {
        val multipleMessages: ByteArray = javaClass.getResource("/multipleMessages.hl7").readBytes()
        val content = readHL7MessagesFromByteArray(multipleMessages)

        // assert that the reader is able to read the bytes and return a string
        assertSame(String::class.java, content::class.java)
    }

    // TEST PARSING FUNCTIONALITY
    @Test
    fun testConvertBatchMessagesToList() {
        // test a single message
        val single = convertBatchMessagesToList(singleMessage)
        assertEquals(1, single.size)

        // test multiple messages
        val multiple = convertBatchMessagesToList(multipleMessages)
        assertEquals(10, multiple.size)

        // test invalid messages
        // convertBatchMessagesToList makes no effort to determine if 
        // the data it receives is valid HL7, so it will still chunk
        // messages even if one or more of the messages are invalid
        val invalid = convertBatchMessagesToList(mixedMessages)
        assertEquals(3, invalid.size)

        // test batch file with a single message
        val batch_single = convertBatchMessagesToList(batchFileSingleMessage)
        assertEquals(1, batch_single.size)

        // test batch file with multiple messages
        val batch_multiple = convertBatchMessagesToList(batchFileMultipleMessages)
        assertEquals(5, batch_multiple.size)

        //test batch file with no messages
        val batch_empty = convertBatchMessagesToList(batchFileNoMessage)
        assertEquals(0, batch_empty.size)

        // test pure text file
        // convertBatchMessagesToList makes no effort to determine if 
        // the data it receives is valid HL7, so it will still chunk
        // messages even if the text itself is not HL7. This should result
        // in a single element being returned, regardless of how large
        // the text is.
        val text = convertBatchMessagesToList(loremIpsum)
        assertEquals(1, text.size)
    }

    @Test
    fun testParse() {
        val processedMessages = parse(mixedMessages)
        val processedText = parse(loremIpsum)

        // test that the method is returning the type we expect
        assertSame(ProcessedMessages::class.java, processedMessages::class.java)
        assertSame(ProcessedMessages::class.java, processedText::class.java)

        // test that it returns the correct number of valid messages
        assertEquals(2, processedMessages.valid_messages.size)
        assertEquals(0, processedText.valid_messages.size)

        // test that it returns the correct number of invalid messages
        assertEquals(1, processedMessages.invalid_messages.size)
        assertEquals(1, processedText.invalid_messages.size)
    }

    // need to clean the text to ensure it's working the way it would in production
    fun cleanText(content: String): String {
        var reg = "[\r\n]".toRegex()
        var cleanedMessage: String = reg.replace(content, "\r")
        reg = "[\\u000b\\u001c]".toRegex()
        cleanedMessage = reg.replace(cleanedMessage, "")
        return cleanedMessage
    }

    // TEST CONVERTING FUNCTIONALITY
    @Test
    fun testConvertMessageToFHIR() {
        // for this test, we'll actually want to Mock hitting the FHIR server
        // so I'll need help getting this set up
        assertEquals(true, true)
    }
    
    // TEST VALIDATING FUNCTIONALITY
    @Test
    fun testIsValidHL7Message() {
        // test that it correctly validates a single valid message
        val cleanedSingleMessage = cleanText(singleMessage)
        assertEquals(true, isValidHL7Message(cleanedSingleMessage))

        // test that it correctly invalidates a single invalid message
        val cleanedSingleInvalidMessage = cleanText(singleInvalidMessage)
        assertEquals(false, isValidHL7Message(cleanedSingleInvalidMessage))

        // test that it correctly invalidates text that is not HL7
        val cleanedLoremIpsum = cleanText(loremIpsum)
        assertEquals(false, isValidHL7Message(cleanedLoremIpsum))
    }
}