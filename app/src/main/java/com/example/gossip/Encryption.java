package com.example.gossip;

public class Encryption {
    public String encrypter(String msg){
        String[] msgs = msg.split(" ");
        String encoded_message = "";
        for(String m: msgs){
            int l = m.length();
            String enc_msg = "";
            char enc_m;
            for (int i = 0; i<m.length(); i++){
                enc_m = (char) (m.charAt(i)+l);
                enc_msg += enc_m;
            }
            encoded_message += enc_msg + " ";
        }
        return encoded_message;
    }

    public String decrypter(String msg){
        String[] msgs = msg.split(" ");
        String decoded_message = "";
        for(String m: msgs){
            int l = m.length();
            String enc_msg = "";
            char enc_m;
            for (int i = 0; i<m.length(); i++){
                enc_m = (char) (m.charAt(i)-l);
                enc_msg += enc_m;
            }
            decoded_message += enc_msg + " ";
        }
        return decoded_message;
    }
}
