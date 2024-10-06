package ru.job4j.pooh;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConsumerClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1", 9000);
        try (PrintWriter out = new PrintWriter(socket.getOutputStream());
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("intro;queue;weather");
            out.flush();
            while (true) {
                String text = input.readLine();
                System.out.println(text);
            }
        }
    }
}
