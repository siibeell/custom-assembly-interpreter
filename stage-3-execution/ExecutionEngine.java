// Stage 3: Execution Engine
// Simulates execution of custom 8-bit assembly instructions
// Includes register, memory and flag management

import java.io.*;
import java.util.*;

public class ExecutionEngine {

    // Registerlar
    static int AX = 0;
    static int BX = 0;
    static int CX = 0;
    static int DX = 0;

    // Bellek
    static int[] RAM = new int[256];

    // Bayraklar
    static boolean SifirBayrak = false;     //sonuc sıfıra eşit değilse false eşitse true
    static boolean IsaretBayrak = false;    //sonuc pozitir ise false negatif ise true
    static boolean TasmaBayrak = false;     //taşma var ise true

    // Program Sayacı
    static int PC = 0;

    static Scanner giris = new Scanner(System.in);

    //8-BIT DÖNÜŞÜM
    public static int to8bit(int x){

        while (x > 127)
            x -= 256;

        while (x < -128)
            x += 256;

        return x;
    }

    //Aynı mı?
    public static boolean ayniMi(String s1, String s2){
        if(s1.length() != s2.length()) return false;

        for(int i=0; i<s1.length(); i++){
            if(s1.charAt(i) != s2.charAt(i)){
                return false;
            }
        }
        return true;
    }

    //Baştaki boşlukları atla
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

    //Bayrak Güncelle
    public static void bayrakGuncelle(int a, int b, int sonuc, char islem){

        SifirBayrak  = (sonuc == 0);
        IsaretBayrak = (sonuc < 0);
        TasmaBayrak  = false;

        // TOP için taşma (+)
        if(islem == '+'){
            if((a > 0 && b > 0 && sonuc < 0) || (a < 0 && b < 0 && sonuc > 0)){
                TasmaBayrak = true;
            }
        }

        // CIK için taşma (-)
        if(islem == '-'){
            if((a > 0 && b < 0 && sonuc < 0) || (a < 0 && b > 0 && sonuc > 0)){
                TasmaBayrak = true;
            }
        }

        // CRP için taşma (*)
        if(islem == '*'){
            long carp = (long)a * (long)b;
            if(carp > 127 || carp < -128){
                TasmaBayrak = true;
            }
        }
    }

    //Operand Değeri Alma
    public static int operandDegeri(String op){

        // Register mı?
        if(ayniMi(op,"AX")) return AX;
        if(ayniMi(op,"BX")) return BX;
        if(ayniMi(op,"CX")) return CX;
        if(ayniMi(op,"DX")) return DX;

        // Bellek mi?
        if(op.startsWith("[") && op.endsWith("]")){
            String ic = op.substring(1, op.length()-1);

            int adres;

            // İçerik register ise
            if(ayniMi(ic,"AX")) adres = AX;
            else if(ayniMi(ic,"BX")) adres = BX;
            else if(ayniMi(ic,"CX")) adres = CX;
            else if(ayniMi(ic,"DX")) adres = DX;
            else adres = Integer.parseInt(ic);

            //GÜvenlik Kontrolü
            if(adres < 0 || adres >= 256){
                System.out.println("HATA: Geçersiz bellek adresi = " + adres);
                return 0;
            }

            return RAM[adres];
        }

        //Sabit Sayı
        return Integer.parseInt(op);
    }

    //Register Yazma
    public static void registerYaz(String op, int deger){
        if(ayniMi(op,"AX")) AX = deger;
        else if(ayniMi(op,"BX")) BX = deger;
        else if(ayniMi(op,"CX")) CX = deger;
        else if(ayniMi(op,"DX")) DX = deger;
    }

    public static void komutIslet(ArrayList<String> tokenlar, HashMap<String,Integer> etiketler){

        String komut = tokenlar.get(0);

        //ATM Komutu
        if(ayniMi(komut, "ATM")){
            String A = tokenlar.get(1);
            String B = tokenlar.get(2);

            int degerB = to8bit(operandDegeri(B));

            // Register yazımı
            if(ayniMi(A,"AX") || ayniMi(A,"BX") || ayniMi(A,"CX") || ayniMi(A,"DX")){
                registerYaz(A, degerB);
            }
            // Bellek yazımı
            else if(A.charAt(0)=='['){
                String ic = A.substring(1, A.length()-1);
                int adres;

                if(ayniMi(ic,"AX")) adres = AX;
                else if(ayniMi(ic,"BX")) adres = BX;
                else if(ayniMi(ic,"CX")) adres = CX;
                else if(ayniMi(ic,"DX")) adres = DX;
                else adres = Integer.parseInt(ic);

                if(adres < 0 || adres >= 256){
                    System.out.println("HATA: Gecersiz bellek adresi " + adres);
                    return;
                }

                RAM[adres] = degerB;
            }
            return;
        }

        //TOP Komutu
        if(ayniMi(komut, "TOP")){
            String A = tokenlar.get(1);
            String B = tokenlar.get(2);

            int a = operandDegeri(A);
            int b = operandDegeri(B);

            int sonuc = to8bit(a + b);

            SifirBayrak  = (sonuc == 0);
            IsaretBayrak = (sonuc < 0);
            TasmaBayrak  = ((a > 0 && b > 0 && sonuc < 0) || (a < 0 && b < 0 && sonuc > 0));

            if(ayniMi(A,"AX") || ayniMi(A,"BX") || ayniMi(A,"CX") || ayniMi(A,"DX")){
                registerYaz(A, sonuc);
            }
            else if(A.charAt(0)=='['){
                String ic = A.substring(1, A.length()-1);
                int adres;

                if(ayniMi(ic,"AX")) adres = AX;
                else if(ayniMi(ic,"BX")) adres = BX;
                else if(ayniMi(ic,"CX")) adres = CX;
                else if(ayniMi(ic,"DX")) adres = DX;
                else adres = Integer.parseInt(ic);

                if(adres < 0 || adres >= 256){
                    System.out.println("HATA: Gecersiz bellek adresi " + adres);
                    return;
                }

                RAM[adres] = sonuc;
            }
            return;
        }

        //CRP Komutu
        if(ayniMi(komut, "CRP")){
            String A = tokenlar.get(1);
            String B = tokenlar.get(2);

            int a = operandDegeri(A);
            int b = operandDegeri(B);

            long carpim = (long)a * (long)b;
            int sonuc = to8bit((int)carpim);

            SifirBayrak  = (sonuc == 0);
            IsaretBayrak = (sonuc < 0);
            TasmaBayrak  = (carpim > 127 || carpim < -128);

            if(ayniMi(A,"AX") || ayniMi(A,"BX") || ayniMi(A,"CX") || ayniMi(A,"DX")){
                registerYaz(A, sonuc);
            }
            else if(A.charAt(0)=='['){
                String ic = A.substring(1, A.length()-1);
                int adres;

                if(ayniMi(ic,"AX")) adres = AX;
                else if(ayniMi(ic,"BX")) adres = BX;
                else if(ayniMi(ic,"CX")) adres = CX;
                else if(ayniMi(ic,"DX")) adres = DX;
                else adres = Integer.parseInt(ic);

                if(adres < 0 || adres >= 256){
                    System.out.println("HATA: Gecersiz bellek adresi " + adres);
                    return;
                }
                RAM[adres] = sonuc;
            }
            return;
        }

        //CIK Komutu
        if(ayniMi(komut, "CIK")){
            String A = tokenlar.get(1);
            String B = tokenlar.get(2);

            int a = operandDegeri(A);
            int b = operandDegeri(B);

            int sonuc = to8bit(a - b);

            SifirBayrak  = (sonuc == 0);
            IsaretBayrak = (sonuc < 0);
            TasmaBayrak  = ((a > 0 && b < 0 && sonuc < 0) || (a < 0 && b > 0 && sonuc > 0));

            if(ayniMi(A,"AX") || ayniMi(A,"BX") || ayniMi(A,"CX") || ayniMi(A,"DX")){
                registerYaz(A, sonuc);
            }
            else if(A.charAt(0)=='['){
                String ic = A.substring(1, A.length()-1);
                int adres;

                if(ayniMi(ic,"AX")) adres = AX;
                else if(ayniMi(ic,"BX")) adres = BX;
                else if(ayniMi(ic,"CX")) adres = CX;
                else if(ayniMi(ic,"DX")) adres = DX;
                else adres = Integer.parseInt(ic);

                if(adres < 0 || adres >= 256){
                    System.out.println("HATA: Gecersiz bellek adresi " + adres);
                    return;
                }
                RAM[adres] = sonuc;
            }
            return;
        }

        //BOL Komutu
        if(ayniMi(komut, "BOL")){
            String A = tokenlar.get(1);
            String B = tokenlar.get(2);

            int a = operandDegeri(A);
            int b = operandDegeri(B);

            if(b == 0){
                System.out.println("HATA: Sıfıra bolunme!");
                return;
            }

            int sonuc = to8bit(a / b);

            SifirBayrak = (sonuc == 0);
            IsaretBayrak = (sonuc < 0);
            TasmaBayrak = false;

            if(ayniMi(A,"AX") || ayniMi(A,"BX") || ayniMi(A,"CX") || ayniMi(A,"DX")){
                registerYaz(A, sonuc);
            }
            else if(A.charAt(0)=='['){
                String ic = A.substring(1, A.length()-1);
                int adres;

                if(ayniMi(ic,"AX")) adres = AX;
                else if(ayniMi(ic,"BX")) adres = BX;
                else if(ayniMi(ic,"CX")) adres = CX;
                else if(ayniMi(ic,"DX")) adres = DX;
                else adres = Integer.parseInt(ic);

                if(adres < 0 || adres >= 256){
                    System.out.println("HATA: Gecersiz bellek adresi " + adres);
                    return;
                }
                RAM[adres] = sonuc;
            }
            return;
        }

        //VE Komutu
        if(ayniMi(komut, "VE")){
            String A = tokenlar.get(1);
            String B = tokenlar.get(2);

            int sonuc = to8bit(operandDegeri(A) & operandDegeri(B));

            SifirBayrak = (sonuc == 0);
            IsaretBayrak = (sonuc < 0);
            TasmaBayrak = false;

            if(ayniMi(A,"AX") || ayniMi(A,"BX") || ayniMi(A,"CX") || ayniMi(A,"DX")){
                registerYaz(A, sonuc);
            }
            else if(A.charAt(0)=='['){
                String ic = A.substring(1, A.length()-1);
                int adres;

                if(ayniMi(ic,"AX")) adres = AX;
                else if(ayniMi(ic,"BX")) adres = BX;
                else if(ayniMi(ic,"CX")) adres = CX;
                else if(ayniMi(ic,"DX")) adres = DX;
                else adres = Integer.parseInt(ic);

                if(adres < 0 || adres >= 256){
                    System.out.println("HATA: Gecersiz bellek adresi " + adres);
                    return;
                }
                RAM[adres] = sonuc;
            }
            return;
        }

        //VEY Komutu
        if(ayniMi(komut, "VEY")){
            String A = tokenlar.get(1);
            String B = tokenlar.get(2);

            int sonuc = to8bit(operandDegeri(A) | operandDegeri(B));

            SifirBayrak = (sonuc == 0);
            IsaretBayrak = (sonuc < 0);
            TasmaBayrak = false;

            if(ayniMi(A,"AX") || ayniMi(A,"BX") || ayniMi(A,"CX") || ayniMi(A,"DX")){
                registerYaz(A, sonuc);
            }
            else if(A.charAt(0)=='['){
                String ic = A.substring(1, A.length()-1);
                int adres;

                if(ayniMi(ic,"AX")) adres = AX;
                else if(ayniMi(ic,"BX")) adres = BX;
                else if(ayniMi(ic,"CX")) adres = CX;
                else if(ayniMi(ic,"DX")) adres = DX;
                else adres = Integer.parseInt(ic);

                if(adres < 0 || adres >= 256){
                    System.out.println("HATA: Gecersiz bellek adresi " + adres);
                    return;
                }
                RAM[adres] = sonuc;
            }
            return;
        }

        //DEG Komutu
        if(ayniMi(komut, "DEG")){
            String A = tokenlar.get(1);

            int sonuc = to8bit(~operandDegeri(A));

            SifirBayrak = (sonuc == 0);
            IsaretBayrak = (sonuc < 0);
            TasmaBayrak = false;

            if(ayniMi(A,"AX") || ayniMi(A,"BX") || ayniMi(A,"CX") || ayniMi(A,"DX")){
                registerYaz(A, sonuc);
            }
            else if(A.charAt(0)=='['){
                String ic = A.substring(1, A.length()-1);
                int adres;

                if(ayniMi(ic,"AX")) adres = AX;
                else if(ayniMi(ic,"BX")) adres = BX;
                else if(ayniMi(ic,"CX")) adres = CX;
                else if(ayniMi(ic,"DX")) adres = DX;
                else adres = Integer.parseInt(ic);

                if(adres < 0 || adres >= 256){
                    System.out.println("HATA: Gecersiz bellek adresi " + adres);
                    return;
                }
                RAM[adres] = sonuc;
            }
            return;
        }

        //OKU Komutu
        if(ayniMi(komut, "OKU")){
            String A = tokenlar.get(1);

            System.out.print(A + " için değer giriniz: ");
            int kullaniciDegeri = giris.nextInt();

            if(ayniMi(A,"AX") || ayniMi(A,"BX") || ayniMi(A,"CX") || ayniMi(A,"DX")){
                registerYaz(A, kullaniciDegeri);
            }
            else if(A.charAt(0)=='['){
                String ic = A.substring(1, A.length()-1);
                int adres;

                if(ayniMi(ic,"AX")) adres = AX;
                else if(ayniMi(ic,"BX")) adres = BX;
                else if(ayniMi(ic,"CX")) adres = CX;
                else if(ayniMi(ic,"DX")) adres = DX;
                else adres = Integer.parseInt(ic);

                if(adres < 0 || adres >= 256){
                    System.out.println("HATA: Gecersiz bellek adresi " + adres);
                    return;
                }
                RAM[adres] = kullaniciDegeri;
            }
            return;
        }

        //YAZ Komutu
        if(ayniMi(komut, "YAZ")){
            System.out.println(operandDegeri(tokenlar.get(1)));
            return;
        }

        //DALLANMALAR
        //D (Şartsız Dallanma)
        if(ayniMi(komut, "D")){
            String etiket = tokenlar.get(1);
            if(etiketler.containsKey(etiket)){
                PC = etiketler.get(etiket);
            }
            return;
        }

        // DE (eşitse yani sonuç == 0)
        if(ayniMi(komut, "DE")){
            if(SifirBayrak){
                PC = etiketler.get(tokenlar.get(1));
            }
            return;
        }

        // DED (eşit değilse yani sonuç != 0)
        if(ayniMi(komut, "DED")){
            if(!SifirBayrak){
                PC = etiketler.get(tokenlar.get(1));
            }
            return;
        }

        // DB (büyükse yani sonuç > 0)
        if(ayniMi(komut, "DB")){
            // sonuç pozitif ise IsaretBayrak=false ve SifirBayrak=false
            if(!IsaretBayrak && !SifirBayrak){
                PC = etiketler.get(tokenlar.get(1));
            }
            return;
        }

       // DBE (büyük veya eşitse yani sonuç >= 0)
        if(ayniMi(komut, "DBE")){
            // sonuç pozitif ise !IsaretBayrak && !SifirBayrak
            // sonuç sıfır ise SifirBayrak
            if(!IsaretBayrak || SifirBayrak){
                PC = etiketler.get(tokenlar.get(1));
            }
            return;
        }

        // DK (küçükse yani sonuç < 0)
        if(ayniMi(komut, "DK")){
            // negatif sonuç ise IsaretBayrak = true
            if(IsaretBayrak){
                PC = etiketler.get(tokenlar.get(1));
            }
            return;
        }

       // DKE (küçük veya eşitse yani sonuç <= 0)
        if(ayniMi(komut, "DKE")){
            // negatif sonuç ise IsaretBayrak = true
            // sıfır ise SifirBayrak = true
            if(IsaretBayrak || SifirBayrak){
                PC = etiketler.get(tokenlar.get(1));
            }
            return;
        }

    }

    public static ArrayList<String> tokenAyir(String satir){
        ArrayList<String> tokenlar = new ArrayList<>();
        String token = "";
        int i = 0;

        //Baştaki boşlukları atla
        i = boslukAtla(satir, i);

        int bas = i;
        boolean etiketVar = false;
        int kolonIndex = -1;

        //Etiket kontrolü (ETIKET:)
        for(int k = bas; k < satir.length(); k++){
            char ch = satir.charAt(k);

            if(ch == ':'){
                etiketVar = true;
                kolonIndex = k;
                break;
            }
            if(ch==' ' || ch=='\t'){
                break;
            }
        }

        //Etiket varsa onu atla - komut kısmından devam et
        if(etiketVar){
            i = kolonIndex + 1;
            i = boslukAtla(satir, i);
        }

        token = "";

        //Komut ve Operandları oku
        for(; i < satir.length(); i++){
            char ch = satir.charAt(i);

            //Bellekteki operand
            if(ch == '['){
                String temp = "";
                i++;

                while(i < satir.length() && satir.charAt(i) != ']'){
                    char ic = satir.charAt(i);

                    // İçeride boşluk varsa sil
                    if(ic != ' ' && ic != '\t'){
                        temp += ic;
                    }
                    i++;
                }

                //Kapanış
                tokenlar.add("[" + temp + "]");
                continue;
            }

            // Normal boşluklar - token sonu
            if(ch==' ' || ch=='\t'){
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
            tokenlar.add(token);
        }
        
        return tokenlar;
    }
    public static void main(String[] args) throws Exception{

        System.out.println("Dosya adını giriniz: ");
        String dosyaAdi = giris.nextLine();

        FileReader dosya = new FileReader(dosyaAdi);
        BufferedReader okuyucu = new BufferedReader(dosya);

        String[] programSatirlari = new String[300];
        int satirSayisi=0;

        String satir = okuyucu.readLine();

        while(satir != null){
            programSatirlari[satirSayisi] = satir;
            satirSayisi++;
            satir = okuyucu.readLine();
        }

        okuyucu.close();
        PC = 0;

        HashMap<String,Integer> etiketTablosu = new HashMap<>();

            for(int satirNo = 0; satirNo < satirSayisi; satirNo++){
                String s = programSatirlari[satirNo];
                int i = boslukAtla(s, 0);

                int bas = i;
                int kolonIndex = -1;

                // etiket kontrolü
                for(; i < s.length(); i++){
                    char ch = s.charAt(i);
                    if(ch == ':'){
                        kolonIndex = i;
                        break;
                    }
                    if(ch==' ' || ch=='\t'){
                        break;
                    }
                }

                if(kolonIndex != -1){
                    String etiketAdi = s.substring(bas, kolonIndex);
                    etiketTablosu.put(etiketAdi, satirNo);
                }
        }

        while(PC < satirSayisi){
            String komutSatiri = programSatirlari[PC];
            ArrayList<String> tokenlar = tokenAyir(komutSatiri);

            int eskiPC = PC;

            if(tokenlar.size() > 0){
                komutIslet(tokenlar, etiketTablosu);
            }

            //Komut PC’yi değiştirmediyse normal ilerle
            if(PC == eskiPC){
                PC++;
            }
        }
        System.out.println("--- REGISTER DURUMLARI ---");
        System.out.println("AX = " + AX);
        System.out.println("BX = " + BX);
        System.out.println("CX = " + CX);
        System.out.println("DX = " + DX);
    }
}