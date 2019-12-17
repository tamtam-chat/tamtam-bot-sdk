package chat.tamtam.bot.chat;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author alexandrchuprin
 */
public class CommandLineParserTest {
    @Test
    public void test1() {
        CommandLine commandLine = CommandLineParser.tryParse(null);
        assertThat(commandLine, is(nullValue()));
    }

    @Test
    public void test2() {
        CommandLine commandLine = CommandLineParser.tryParse("");
        assertThat(commandLine, is(nullValue()));
    }

    @Test
    public void test3() {
        CommandLine commandLine = CommandLineParser.tryParse("notcommand");
        assertThat(commandLine, is(nullValue()));
    }

    @Test
    public void test4() {
        CommandLine commandLine = CommandLineParser.tryParse("/command");
        assertThat(commandLine.getKey(), is("command"));
        assertThat(commandLine.getArgs(), is(new String[0]));
    }

    @Test
    public void test5() {
        CommandLine commandLine = CommandLineParser.tryParse("/");
        assertThat(commandLine, is(nullValue()));

        commandLine = CommandLineParser.tryParse("/ ");
        assertThat(commandLine, is(nullValue()));
    }

    @Test
    public void test6() {
        CommandLine commandLine = CommandLineParser.tryParse("/command arg1 arg2");
        assertThat(commandLine.getKey(), is("command"));
        assertThat(commandLine.getArgs(), is(new String[]{"arg1", "arg2"}));
    }

    @Test
    public void test7() {
        CommandLine commandLine = CommandLineParser.tryParse("/command \"arg1 with spaces\" arg2");
        assertThat(commandLine.getKey(), is("command"));
        assertThat(commandLine.getArgs(), is(new String[]{"arg1 with spaces", "arg2"}));
    }

    @Test
    public void test8() {
        CommandLine commandLine = CommandLineParser.tryParse("/command \"arg1 with spaces and \\\" quote\" arg2");
        assertThat(commandLine.getKey(), is("command"));
        assertThat(commandLine.getArgs(), is(new String[]{"arg1 with spaces and \" quote", "arg2"}));
    }
}