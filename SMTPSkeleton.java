package smtpskeleton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;


public class SMTPSkeleton {

    private final static int smtp_port = 587;
    private final static String mailServer = "smtp.sendgrid.net";

    public static void main(String[] args) throws IOException {

        Socket smtpSocket = null;
        String welcome = maincmd(smtpSocket);
        System.out.println(welcome);

    }

    public static String maincmd(Socket smtpSocket) throws IOException,UnknownHostException{
        String welcome = null;
        try {
            InetAddress mailHost = InetAddress.getByName(mailServer);
            InetAddress localHost = InetAddress.getLocalHost();
            //smtpSocket = new Socket(mailHost, smtp_port);
            BufferedReader in = null;//new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
            PrintWriter pr = null;//new PrintWriter(smtpSocket.getOutputStream(), true);
            String initialID = null;//in.readLine();
            //System.out.println(initialID);
            try {
                smtpSocket = new Socket();
                smtpSocket.connect(new InetSocketAddress(mailHost, smtp_port), 20000);
                in = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
                pr = new PrintWriter(smtpSocket.getOutputStream(), true);
                initialID = in.readLine();
                System.out.println(initialID);
            }
            catch(SocketTimeoutException e){
                System.out.println("Connection is taking to long");
                exit(1);
            }

            while (!initialID.startsWith("220")) {
                //System.out.println("220 reply not received from server.");
                try {
                    smtpSocket.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(initialID.startsWith("4")){
                    break;
                }
            }

           // List<String> lines = Files.readAllLines(Paths.get("input.txt"), StandardCharsets.UTF_8);

            //System.out.println(lines.get(0));

            System.out.println("Enter smtp command:\r\nStart with saying 'HELO' to the server\r\n");
            Scanner sc = new Scanner(System.in);
            String command = null;
            //   System.out.println(command);

            command = sc.nextLine();
            while (!command.equals("HELO")) {
                pr.println(command + " " + localHost.getHostName());
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("You may try 'HELO'");
                command = sc.nextLine();
            }
            welcome = helocmd(command,smtpSocket);
            //exit(1);

            while (!welcome.startsWith("250")) {
                System.out.println("250 reply not received from server.");
                welcome = helocmd(command,smtpSocket);//exit(1);
            }

            pr.println("AUTH LOGIN");
            welcome = in.readLine();
            System.out.println(welcome);
            pr.println("anN1bHRhbmFqaXNoYQ==");
            welcome = in.readLine();
            System.out.println(welcome);
            pr.println("dHlwZXdyaXR0ZXIxLg==");
            welcome = in.readLine();
            System.out.println(welcome);

            System.out.println("Write 'MAIL FROM:<yourmail@mail.com>' to send mail\r\n");
            command = sc.nextLine();
            while(!command.contains("MAIL FROM:")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("You may try 'MAIL FROM:sender'");
                command = sc.nextLine();
            }
            welcome = mailcmd(command,smtpSocket);
            //pr.flush();

            while (!welcome.startsWith("250")) {
                System.out.println("Sender mail is not accepted by server.");
                welcome = mailcmd(command,smtpSocket);
            }

            System.out.print("Do you want to continue?\nPress enter to continue\nType 'RSET' to reset\n");
            command = sc.nextLine();
            while(command.equals("RSET")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("Start from beginning");

                //mail from again
                command = sc.nextLine();
                while(!command.contains("MAIL FROM:")){
                    pr.println(command);
                    welcome = in.readLine();
                    System.out.println(welcome);
                    System.out.println("You may try 'MAIL FROM:sender'");
                    command = sc.nextLine();
                }
                welcome = mailcmd(command,smtpSocket);
                while (!welcome.startsWith("250")) {
                    System.out.println("Sender mail is not accepted by server.");
                    welcome = mailcmd(command,smtpSocket);
                }
                System.out.print("Do you want to continue?\nPress enter to continue\nType 'RSET' to reset\n");
                command = sc.nextLine();
            }
            pr.println(command);
            welcome = in.readLine();
            //System.out.println(welcome);


            System.out.println("Number of recipients : ");
            Scanner scint = new Scanner(System.in);
            int numofres = scint.nextInt();
            //  int numofres = Integer.parseInt(lines.get(2));

            System.out.println("Write 'RCPT TO:<othermail@mail.com>' to send mail\r\n");
            command = sc.nextLine();
            while(!command.contains("RCPT TO:")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("You may try 'RCPT TO:sender'");
                command = sc.nextLine();
            }
            for(int i=numofres;i>0;i--){
                welcome = mailcmd(command,smtpSocket);
                command = sc.nextLine();
            }

            while (!welcome.startsWith("250")) {
                System.out.println("Receiver mail is not accepted from server.");
                welcome = mailcmd(command,smtpSocket);
            }

            System.out.print("Do you want to continue?\nPress enter to continue\nType 'RSET' to reset\n");
            command = sc.nextLine();
            while(command.equals("RSET")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("Start from beginning");

                //mail from again
                command = sc.nextLine();
                while(!command.contains("MAIL FROM:")){
                    pr.println(command);
                    welcome = in.readLine();
                    System.out.println(welcome);
                    System.out.println("You may try 'MAIL FROM:sender'");
                    command = sc.nextLine();
                }
                welcome = mailcmd(command,smtpSocket);
                //pr.flush();

                while (!welcome.startsWith("250")) {
                    System.out.println("Sender mail is not accepted by server.");
                    welcome = mailcmd(command,smtpSocket);
                }

                System.out.print("Number of recipients : ");
                scint = new Scanner(System.in);
                numofres = scint.nextInt();

                System.out.println("Write 'RCPT TO:<othermail@mail.com>' to send mail\r\n");
                command = sc.nextLine();
                while(!command.contains("RCPT TO:")){
                    pr.println(command);
                    welcome = in.readLine();
                    System.out.println(welcome);
                    System.out.println("You may try 'RCPT TO:sender'");
                    command = sc.nextLine();
                }
                for(int i=numofres;i>0;i--){
                    welcome = mailcmd(command,smtpSocket);
                    for(int k=numofres-1;k>0;k--) {
                        command = sc.nextLine();
                    }
                }

                while (!welcome.startsWith("250")) {
                    System.out.println("Receiver mail is not accepted from server.");
                    welcome = mailcmd(command,smtpSocket);
                }
                System.out.print("Do you want to continue?\nPress enter to continue\nType 'RSET' to reset\n");
                command = sc.nextLine();
            }
            pr.println(command);
            welcome = in.readLine();
            //System.out.println(welcome);


            System.out.println("Write 'DATA' to start composing the mail\r\n");
            command = sc.nextLine();
            while(!command.equals("DATA")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("You may try 'DATA'");
                command = sc.nextLine();
            }
            welcome = mailcmd(command,smtpSocket);

            while (!welcome.startsWith("354")) {
                System.out.println("Server did not grant permission for email.");
                welcome = mailcmd(command,smtpSocket);
            }

            System.out.print("Start composing with 'Subject:<subject>','From:<yourmail@mail.com>','To:<othermail@mail.com>'"
                    +",'message body'.\r\n"+"For attachment include MIME version\r\n" +"To send the mail '<ENTER>.<ENTER>'\r\n");
            command = sc.nextLine();
            while(!command.contains("Subject:")){
                System.out.println("You may try 'Subject:'");
                command = sc.nextLine();
            }
            pr.println(command);

            command = sc.nextLine();
            while(!command.contains("From:")){
                System.out.println("You may try or 'From:'");
                command = sc.nextLine();
            }
            pr.println(command);

            command = sc.nextLine();
            while(!command.contains("To:")){
                System.out.println("You may try 'To:'");
                command = sc.nextLine();
            }
            for(int i=numofres;i>0;i--){
                pr.println(command);
            }

            command = sc.nextLine();
            while(!command.equals(".")){
                pr.println(command);
                command = sc.nextLine();
            }

            welcome = mailcmd(command,smtpSocket);
            while (!welcome.startsWith("250")) {
                System.out.println("Mail is not delivered.");
                welcome = mailcmd(command,smtpSocket);
            }

            System.out.print("Do you want to quit?\nTo quit type'QUIT'\nTo continue press <ENTER>\n");
            command = sc.nextLine();
            while(!command.equals("QUIT")){
                welcome = recursivemaincmd(smtpSocket);
                System.out.println(welcome);
                System.out.print("Do you want to quit?\nTo quit type'QUIT'\nTo continue press <ENTER>\n");
                command = sc.nextLine();
            }
            welcome = mailcmd(command,smtpSocket);
            while (!welcome.startsWith("221")) {
                System.out.println("Connection from server to client not closed.");
                welcome = mailcmd(command,smtpSocket);
            }
        }
        finally
        {
            if( smtpSocket != null ){
                smtpSocket.close();
            }
        }
        return welcome;
    }

    public static String helocmd(String command, Socket smtpSocket){

        String welcome=null;
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        PrintWriter pr = null;
        try {
            in = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
            pr = new PrintWriter(smtpSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            long starttime = System.currentTimeMillis();
            long endtime = starttime + 20000;
            while (System.currentTimeMillis() <= endtime) {
                int flag = 1;
                pr.println(command + " " + localHost.getHostName());
                flag++;
                if (flag > 1) {
                    break;
                }
            }
            if (System.currentTimeMillis() > endtime) {
                System.out.println("SMTP Command taking too long.");
                exit(1);
            }
            welcome = in.readLine();
            System.out.println(welcome);
        }catch(IOException e){
            e.printStackTrace();
        }
        return  welcome;
    }

    public static String mailcmd(String command, Socket smtpSocket){

        String welcome=null;

        BufferedReader in = null;
        PrintWriter pr = null;
        try {
            in = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
            pr = new PrintWriter(smtpSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            long starttime = System.currentTimeMillis();
            long endtime = starttime + 20000;
            while (System.currentTimeMillis() <= endtime) {
                int flag = 1;
                pr.println(command);
                flag++;
                if (flag > 1) {
                    break;
                }
            }
            if (System.currentTimeMillis() > endtime) {
                System.out.println("SMTP Command taking too long.");
                exit(1);
            }
            welcome = in.readLine();
            System.out.println(welcome);
        }catch(IOException e){
            e.printStackTrace();
        }
        return  welcome;
    }

    public static String recursivemaincmd(Socket smtpSocket) throws IOException,UnknownHostException{
        String welcome = null;
        try {
            InetAddress mailHost = InetAddress.getByName(mailServer);
            InetAddress localHost = InetAddress.getLocalHost();
            //smtpSocket = new Socket(mailHost, smtp_port);
            BufferedReader in = null;//new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
            PrintWriter pr = null;//new PrintWriter(smtpSocket.getOutputStream(), true);
            String initialID = null;//in.readLine();
            //System.out.println(initialID);
            try {
                smtpSocket = new Socket();
                smtpSocket.connect(new InetSocketAddress(mailHost, smtp_port), 20000);
                in = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
                pr = new PrintWriter(smtpSocket.getOutputStream(), true);
                initialID = in.readLine();
                System.out.println(initialID);
            }
            catch(SocketTimeoutException e){
                System.out.println("Connection is taking to long");
                exit(1);
            }

            while (!initialID.startsWith("220")) {
                //System.out.println("220 reply not received from server.");
                try {
                    smtpSocket.wait(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(initialID.startsWith("4")){
                    break;
                }
            }


            System.out.println("Enter smtp command:\r\nStart with saying 'HELO' to the server\r\n");
            Scanner sc = new Scanner(System.in);
            String command = null;
            //   System.out.println(command);

            command = sc.nextLine();
            while (!command.equals("HELO")) {
                pr.println(command + " " + localHost.getHostName());
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("You may try 'HELO'");
                command = sc.nextLine();
            }
            welcome = helocmd(command,smtpSocket);
            //exit(1);

            while (!welcome.startsWith("250")) {
                System.out.println("250 reply not received from server.");
                welcome = helocmd(command,smtpSocket);//exit(1);
            }

            pr.println("AUTH LOGIN");
            welcome = in.readLine();
            System.out.println(welcome);
            pr.println("anN1bHRhbmFqaXNoYQ==");
            welcome = in.readLine();
            System.out.println(welcome);
            pr.println("dHlwZXdyaXR0ZXIxLg==");
            welcome = in.readLine();
            System.out.println(welcome);

            System.out.println("Write 'MAIL FROM:<yourmail@mail.com>' to send mail\r\n");
            command = sc.nextLine();
            while(!command.contains("MAIL FROM:")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("You may try 'MAIL FROM:sender'");
                command = sc.nextLine();
            }
            welcome = mailcmd(command,smtpSocket);
            //pr.flush();

            while (!welcome.startsWith("250")) {
                System.out.println("Sender mail is not accepted by server.");
                welcome = mailcmd(command,smtpSocket);
            }

            System.out.print("Do you want to continue?\nPress enter to continue\nType 'RSET' to reset\n");
            command = sc.nextLine();
            while(command.equals("RSET")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("Start from beginning");

                //mail from again
                command = sc.nextLine();
                while(!command.contains("MAIL FROM:")){
                    pr.println(command);
                    welcome = in.readLine();
                    System.out.println(welcome);
                    System.out.println("You may try 'MAIL FROM:sender'");
                    command = sc.nextLine();
                }
                welcome = mailcmd(command,smtpSocket);
                while (!welcome.startsWith("250")) {
                    System.out.println("Sender mail is not accepted by server.");
                    welcome = mailcmd(command,smtpSocket);
                }
                System.out.print("Do you want to continue?\nPress enter to continue\nType 'RSET' to reset\n");
                command = sc.nextLine();
            }
            pr.println(command);
            welcome = in.readLine();
            //System.out.println(welcome);


            System.out.print("Number of recipients : ");
            Scanner scint = new Scanner(System.in);
            int numofres = scint.nextInt();

            System.out.println("Write 'RCPT TO:<othermail@mail.com>' to send mail\r\n");
            command = sc.nextLine();
            while(!command.contains("RCPT TO:")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("You may try 'RCPT TO:sender'");
                command = sc.nextLine();
            }
            for(int i=numofres;i>0;i--){
                welcome = mailcmd(command,smtpSocket);
                for(int k=numofres-1;k>0;k--) {
                    command = sc.nextLine();
                }
            }

            while (!welcome.startsWith("250")) {
                System.out.println("Receiver mail is not accepted from server.");
                welcome = mailcmd(command,smtpSocket);
            }

            System.out.print("Do you want to continue?\nPress enter to continue\nType 'RSET' to reset\n");
            command = sc.nextLine();
            while(command.equals("RSET")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("Start from beginning");

                //mail from again
                command = sc.nextLine();
                while(!command.contains("MAIL FROM:")){
                    pr.println(command);
                    welcome = in.readLine();
                    System.out.println(welcome);
                    System.out.println("You may try 'MAIL FROM:sender'");
                    command = sc.nextLine();
                }
                welcome = mailcmd(command,smtpSocket);
                //pr.flush();

                while (!welcome.startsWith("250")) {
                    System.out.println("Sender mail is not accepted by server.");
                    welcome = mailcmd(command,smtpSocket);
                }

                System.out.print("Number of recipients : ");
                scint = new Scanner(System.in);
                numofres = scint.nextInt();

                System.out.println("Write 'RCPT TO:<othermail@mail.com>' to send mail\r\n");
                command = sc.nextLine();
                while(!command.contains("RCPT TO:")){
                    pr.println(command);
                    welcome = in.readLine();
                    System.out.println(welcome);
                    System.out.println("You may try 'RCPT TO:sender'");
                    command = sc.nextLine();
                }
                for(int i=numofres;i>0;i--){
                    welcome = mailcmd(command,smtpSocket);
                    for(int k=numofres-1;k>0;k--) {
                        command = sc.nextLine();
                    }
                }

                while (!welcome.startsWith("250")) {
                    System.out.println("Receiver mail is not accepted from server.");
                    welcome = mailcmd(command,smtpSocket);
                }
                System.out.print("Do you want to continue?\nPress enter to continue\nType 'RSET' to reset\n");
                command = sc.nextLine();

            }

            pr.println(command);
            welcome = in.readLine();
            //System.out.println(welcome);


            System.out.println("Write 'DATA' to start composing the mail\r\n");
            command = sc.nextLine();
            while(!command.equals("DATA")){
                pr.println(command);
                welcome = in.readLine();
                System.out.println(welcome);
                System.out.println("You may try 'DATA'");
                command = sc.nextLine();
            }
            welcome = mailcmd(command,smtpSocket);

            while (!welcome.startsWith("354")) {
                System.out.println("Server did not grant permission for email.");
                welcome = mailcmd(command,smtpSocket);
            }

            System.out.print("Start composing with 'Subject:<subject>','From:<yourmail@mail.com>','To:<othermail@mail.com>'"
                    +",'message body'.\r\n"+"For attachment include MIME version\r\n" +"To send the mail '<ENTER>.<ENTER>'\r\n");
            command = sc.nextLine();
            while(!command.contains("Subject:")){
                System.out.println("You may try 'Subject:'");
                command = sc.nextLine();
            }
            pr.println(command);

            command = sc.nextLine();
            while(!command.contains("From:")){
                System.out.println("You may try or 'From:'");
                command = sc.nextLine();
            }
            pr.println(command);

            command = sc.nextLine();
            while(!command.contains("To:")){
                System.out.println("You may try 'To:'");
                command = sc.nextLine();
            }
            for(int i=numofres;i>0;i--){
                pr.println(command);
            }

            command = sc.nextLine();
            while(!command.equals(".")){
                pr.println(command);
                command = sc.nextLine();
            }

            welcome = mailcmd(command,smtpSocket);
            while (!welcome.startsWith("250")) {
                System.out.println("Mail is not delivered.");
                welcome = mailcmd(command,smtpSocket);
            }

        }
        finally
        {
            if( smtpSocket != null ){
                smtpSocket.close();
            }
        }

        return welcome;
    }
}