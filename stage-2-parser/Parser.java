// Stage 2: Parser
// Validates syntax rules of the custom assembly language

import java.io.*;
import java.util.*;

import javax.lang.model.util.ElementScanner6;

public class Parser {

    public static boolean bosMu(String satir){
        for(int i=0; i<satir.length(); i++){
            char ch = satir.charAt(i);
            if(ch != ' ' && ch != '\t'){
                return false;
            }
        }
        return true;
    }

    public static int boslukAtla(String satir, int index){
        while(index < satir.length()){
            char ch = satir.charAt(index);
            if(ch != ' ' && ch != '\t'){
                break;
            }
            index++;
        }
        return index;
    }

    public static boolean ayniMi(String s1, String s2){
        if(s1.length() != s2.length()) return false;
        for(int i=0; i<s1.length(); i++){
            if(s1.charAt(i) != s2.charAt(i)) return false;
        }
        return true;
    }

    public static ArrayList<String> tokenAyir(String satir){
    ArrayList<String> tokenlar = new ArrayList<>();
    String token = "";
    int i = 0;

    i = boslukAtla(satir, i);

    for(i=0; i < satir.length(); i++){
        char ch = satir.charAt(i);

        if(ch == ' ' || ch == '\t'){
            if(token.length() > 0){
                tokenlar.add(token);
                token = "";
            }
            i = boslukAtla(satir, i);
            i--;
        }
        else{
            token += ch;
        }
    }

    if(token.length() > 0){
        boolean sadeceBosluk = true;
        for(int j = 0; j < token.length(); j++){
            if(token.charAt(j) != ' ' && token.charAt(j) != '\t'){
                sadeceBosluk = false;
                break;
            }
        }
        if(!sadeceBosluk){
            tokenlar.add(token);
        }
    }

    return tokenlar;
}


    public static boolean komutGecerliMi(String komut){
        String[] komutlar = {
            "ATM", "TOP", "CRP", "CIK", "BOL",
            "VE", "VEY", "DEG",
            "DE", "DED", "DB", "DBE", "DK", "DKE", "D",
            "OKU", "YAZ"
        };

        for(int i=0; i<komutlar.length; i++){
            boolean ayni = true;

            if(komut.length() != komutlar[i].length()){
                ayni = false;
            }else{
                for(int j=0; j<komut.length(); j++){
                    char c1 = komut.charAt(j);
                    char c2 = komutlar[i].charAt(j);
                    if(c1 != c2){
                        ayni = false;
                        break;
                    }
                }
            }
            if(ayni) return true;
        }
        return false;
    }

    public static boolean operandGecerliMi(String op){

        //1. Register kontrolü
        if(op.length() == 2){
            char c1 = op.charAt(0);
            char c2 = op.charAt(1);
            if(c1 == 'A' && c2 == 'X') return true;
            if(c1 == 'B' && c2 == 'X') return true;
            if(c1 == 'C' && c2 == 'X') return true;
            if(c1 == 'D' && c2 == 'X') return true;
        }

        //2. Normal sabit kontrolü(0-255)
        boolean sadeceRakam = true;
        for(int i=0; i<op.length(); i++){
            char ch = op.charAt(i);
            if(ch < '0' || ch > '9'){
                sadeceRakam = false;
                break;
            }
        }

        if(sadeceRakam && op.length() > 0){
            int sayi = 0;

            //karakter karakter sayıya çevir
            for(int k=0; k<op.length(); k++){
                sayi = sayi * 10 + (op.charAt(k) - '0');
            }

            //0–255 aralığındaysa geçerli
            if(sayi >= 0 && sayi <= 255)
                return true;
        }

        //3. Köşeli sabit kontrolü ([0–100])
        if(op.charAt(0) == '[' && op.charAt(op.length()-1) == ']'){
            boolean rakamIcerik = true;
            for(int i=1; i<op.length()-1; i++){
                char ch = op.charAt(i);
                if(ch < '0' || ch > '9'){
                    rakamIcerik = false;
                    break;
                }
            }

            if(rakamIcerik && op.length() > 2){
                int sayi = 0;
                for(int k=1; k<op.length()-1; k++){
                    sayi = sayi * 10 + (op.charAt(k) - '0');
                }

                //Köşeli sabit 0–100 aralığında olmalı
                if(sayi >= 0 && sayi <= 100)
                    return true;
            }
        }

        //4. Etiket kontrolü
        if(op.length() >= 7 && op.length() <= 8){
            if(op.charAt(0) == 'E' && op.charAt(1) == 'T' && op.charAt(2) == 'I'
            && op.charAt(3) == 'K' && op.charAt(4) == 'E' && op.charAt(5) == 'T'){
                boolean sayiVar = true;
                for(int i=6; i<op.length(); i++){
                    char ch = op.charAt(i);
                    if(ch < '0' || ch > '9'){
                        sayiVar = false;
                        break;
                    }
                }
                if(sayiVar) return true;
            }
        }
        return false;
    }

    public static boolean satirGecerliMi(ArrayList<String> tokenlar){
        String komut = "";
        int komutIndex = 0;

        if(tokenlar.get(0).charAt(tokenlar.get(0).length()-1) == ':'){
            if(tokenlar.size() < 2) return false;
            komut = tokenlar.get(1);
            komutIndex = 1;
        }else{
            komut = tokenlar.get(0);
        }

        if(!komutGecerliMi(komut)){
            return false;
        }

        int kalan = tokenlar.size() -(komutIndex+1);

        if(ayniMi(komut, "OKU") || ayniMi(komut, "YAZ")){           //giris-cikis
            if(kalan != 1) return false;
            if(!operandGecerliMi(tokenlar.get(komutIndex+1))) return false;
        }else if(ayniMi(komut, "ATM")){         //atama
            if(kalan != 2) return false;
            if(!operandGecerliMi(tokenlar.get(komutIndex+1))) return false;
            if(!operandGecerliMi(tokenlar.get(komutIndex+2))) return false;
        }else if(ayniMi(komut, "TOP") || ayniMi(komut, "CRP") || ayniMi(komut, "CIK") || ayniMi(komut, "BOL")){         //aritmetik
            if(kalan != 2) return false;
            if(!operandGecerliMi(tokenlar.get(komutIndex+1))) return false;
            if(!operandGecerliMi(tokenlar.get(komutIndex+2))) return false;
        }else if(ayniMi(komut, "VE") || ayniMi(komut, "VEY")){          //mantiksal (iki operandli)
            if(kalan != 2) return false;
            if(!operandGecerliMi(tokenlar.get(komutIndex+1))) return false;
            if(!operandGecerliMi(tokenlar.get(komutIndex+2))) return false;
        }else if(ayniMi(komut, "DEG")){         //mantiksal (tek operandli)
            if(kalan != 1) return false;
            if(!operandGecerliMi(tokenlar.get(komutIndex+1))) return false;
        }else if(ayniMi(komut, "DE") || ayniMi(komut, "DED") || ayniMi(komut, "DB") || ayniMi(komut, "DBE")         //dallanma
        || ayniMi(komut, "DK") || ayniMi(komut, "DKE") || ayniMi(komut, "D")){
            if(kalan != 1) return false;
            if(!operandGecerliMi(tokenlar.get(komutIndex+1))) return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception{
        Scanner giris = new Scanner(System.in);

        System.out.println("Kontrol edilecek dosya adını giriniz: ");
        String dosyaAdi = giris.nextLine();

        FileReader dosya = new FileReader(dosyaAdi);
        BufferedReader okuyucu = new BufferedReader(dosya);

        String satir;
        int sira = 1;
        boolean hataVar = false;

        // 🔹 D komutu için dallanma kontrol değişkenleri
        boolean atlamaModu = false;
        String hedefEtiket = "";

        System.out.println("\nDosya icerigi: \n");

        while((satir = okuyucu.readLine()) != null){
            if(bosMu(satir)) continue;

            ArrayList<String> tokenlar = tokenAyir(satir);

            System.out.print(sira + ": ");
            for(int i = 0; i < tokenlar.size(); i++){
                System.out.print("[" + tokenlar.get(i) + "] ");
            }

            if(!satirGecerliMi(tokenlar)){
                hataVar = true;
                System.out.print("<-- YAZIM HATASI");
            }

            System.out.println();
            sira++;
        }

        if(hataVar)
            System.out.println("\nYAZIM HATASI BULUNMAKTADIR");
        else
            System.out.println("\nYAZIM HATASI BULUNMAMAKTADIR");

        okuyucu.close();
        giris.close();
    }
}