/*
 * CommandLine.java
 *
 * Created on 25. Oktober 2006, 10:03
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Stadt Wien, Peter Pfl�ging <peter.pflaeging@wien.gv.at>
 */
package at.gv.wien.PortableSigner;

import org.apache.commons.cli.*;
import java.io.FileInputStream;

/**
 *
 * @author pfp
 */
public class SignCommandLine {

    public String input = "",  output = "",  signature = "",  password = "",  sigblock = "",  sigimage = "",  comment = "",  reason = "",  location = "",  pwdFile = "",  ownerPwdFile = "",  ownerPwdString = "";
    public byte[] ownerPwd = null;
    public Boolean nogui = false,  finalize = true;
    private Boolean help = false;
    String langcodes;

    /** Creates a new instance of CommandLine */
    public SignCommandLine(String args[]) {
        langcodes = "";
        java.util.Enumeration<String> langCodes =
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/SignatureblockLanguages").getKeys();

        while ( langCodes.hasMoreElements() )  {
            langcodes = langcodes + langCodes.nextElement() + "|";
        }
        langcodes = langcodes.substring(0, langcodes.length()-1);
//        System.out.println("Langcodes: " + langcodes);

        CommandLine cmd;
        Options options = new Options();
        options.addOption("t", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-InputFile"));
        options.addOption("o", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-OutputFile"));
        options.addOption("s", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-SignatureFile"));
        options.addOption("p", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-Password"));
        options.addOption("n", false,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-WithoutGUI"));
        options.addOption("f", false,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-Finalize"));
        options.addOption("h", false,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-Help"));
        options.addOption("b", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-SigBlock")
                + langcodes);
        options.addOption("i", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-SigImage"));
        options.addOption("c", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-SigComment"));
        options.addOption("r", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-SigReason"));
        options.addOption("l", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-SigLocation"));
        options.addOption("pwdfile", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-PasswdFile"));
        options.addOption("ownerpwd", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-OwnerPasswd"));
        options.addOption("ownerpwdfile", true,
                java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n").getString("CLI-OwnerPasswdFile"));

        CommandLineParser parser = new PosixParser();
        HelpFormatter usage = new HelpFormatter();
        try {
            cmd = parser.parse(options, args);
            input = cmd.getOptionValue("t", "");
            output = cmd.getOptionValue("o", "");
            signature = cmd.getOptionValue("s", "");
            password = cmd.getOptionValue("p", "");
            nogui = cmd.hasOption("n");
            help = cmd.hasOption("h");
            finalize = !cmd.hasOption("f");
            sigblock = cmd.getOptionValue("b", "");
            sigimage = cmd.getOptionValue("i", "");
            comment = cmd.getOptionValue("c", "");
            reason = cmd.getOptionValue("r", "");
            location = cmd.getOptionValue("l", "");
            pwdFile = cmd.getOptionValue("pwdfile", "");
            ownerPwdString = cmd.getOptionValue("ownerpwd", "");
            ownerPwdFile = cmd.getOptionValue("ownerpwdfile", "");

            if (cmd.getArgs().length != 0) {
                throw new ParseException(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n")
                        .getString("CLI-UnknownArguments"));
            }
        } catch (ParseException e) {
            System.err.println(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n")
                    .getString("CLI-WrongArguments"));
            usage.printHelp("PortableSigner", options);
            System.exit(3);
        }
        if (nogui) {
            if (input.equals("") || output.equals("") || signature.equals("")) {
                System.err.println(java.util.ResourceBundle
                        .getBundle("at/gv/wien/PortableSigner/i18n")
                        .getString("CLI-MissingArguments"));
                usage.printHelp("PortableSigner", options);
                System.exit(2);
            }
            if (password.equals("")) {
                // password missing
                if (!pwdFile.equals("")) {
                    // read the password from the given file
                    try {
                        FileInputStream pwdfis = new FileInputStream(pwdFile);
                        byte[] pwd = new byte[1024];
                        password = "";
                        try {
                            do {
                                int r = pwdfis.read(pwd);
                                if (r < 0) {
                                    break;
                                }
                                password += new String(pwd);
                                password = password.trim();
                            } while (pwdfis.available() > 0);
                            pwdfis.close();
                        } catch (java.io.IOException ex) {
                        }
                    } catch (java.io.FileNotFoundException fnfex) {
                    }
                } else {
                    // no password file given, read from standard input
                    System.out.print(java.util.ResourceBundle
                            .getBundle("at/gv/wien/PortableSigner/i18n")
                            .getString("CLI-MissingPassword"));
                    byte[] pwd = new byte[1024];
                    password = "";
                    try {
                        do {
                            int r = System.in.read(pwd);
                            if (r < 0) {
                                break;
                            }
                            password += new String(pwd);
                            password = password.trim();
                        } while (System.in.available() > 0);
                    } catch (java.io.IOException ex) {
                    }
                }
            }
            if (!ownerPwdString.equals("") && !ownerPwdFile.equals("")) {
                ownerPwd = new byte[0];
            } else if (!ownerPwdString.equals("")) {
                ownerPwd = ownerPwdString.getBytes();
            } else if (!ownerPwdFile.equals("")) {
                try {
                    FileInputStream pwdfis = new FileInputStream(ownerPwdFile);
                    ownerPwd = new byte[0];
                    byte[] tmp = new byte[1024];
                    byte[] full;
                    try {
                        do {
                            int r = pwdfis.read(tmp);
                            if (r < 0) {
                                break;
                            }
                            //tmp = Arrays.copyOfRange(tmp, 0, r);
                            System.arraycopy(tmp, 0, tmp, 0, r);
                            full = new byte[ownerPwd.length + tmp.length];
                            System.arraycopy(ownerPwd, 0, full, 0, ownerPwd.length);
                            System.arraycopy(tmp, 0, full, ownerPwd.length, tmp.length);
                            ownerPwd = full;
                        } while (pwdfis.available() > 0);
                        pwdfis.close();
                    } catch (java.io.IOException ex) {
                    }
                } catch (java.io.FileNotFoundException fnfex) {
                }
            }

        }
        if (!(langcodes.contains(sigblock)|| sigblock.equals(""))) {
            System.err.println(java.util.ResourceBundle.getBundle("at/gv/wien/PortableSigner/i18n")
                    .getString("CLI-Only-german-english") + langcodes);
            usage.printHelp("PortableSigner", options);
            System.exit(4);
        }
        if (help) {
            usage.printHelp("PortableSigner", options);
            System.exit(1);
        }
//        System.err.println("CMDline: input: " + input);
//        System.err.println("CMDline: output: " + output);
//        System.err.println("CMDline: signature: " + signature);
//        System.err.println("CMDline: password: " + password);
//        System.err.println("CMDline: sigblock: " + sigblock);
//        System.err.println("CMDline: sigimage: " + sigimage);
//        System.err.println("CMDline: comment: " + comment);
//        System.err.println("CMDline: reason: " + reason);
//        System.err.println("CMDline: location: " + location);
//        System.err.println("CMDline: pwdFile: " + pwdFile);
//        System.err.println("CMDline: ownerPwdFile: " + ownerPwdFile);
//        System.err.println("CMDline: ownerPwdString: " + ownerPwdString);
//        System.err.println("CMDline: ownerPwd: " + ownerPwd.toString());
    }
}
