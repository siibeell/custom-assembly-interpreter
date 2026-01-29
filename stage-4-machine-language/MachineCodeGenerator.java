// Stage 4: Machine Language Generation
// Converts assembly instructions into hexadecimal machine code

import java.io.*;
import java.util.*;

public class MachineCodeGenerator {

    static HashMap<String, String> komutOpcode = new HashMap<>();
    static HashMap<String, String> operandOpcode = new HashMap<>();
    static HashMap<String, Integer> etiketler = new HashMap<>();

    static ArrayList<String> satirlar = new ArrayList<>();
    static StringBuilder tumHex = new StringBuilder();

    public static void main(String[] args) throws Exception {

        opcodeYukle();

        Scanner giris = new Scanner(System.in);
        System.out.print("Dosya adını gir: ");
        String dosyaAdi = giris.nextLine();

        dosyaOku(dosyaAdi);
        etiketleriBul();
        cevirVeYazdir();
    }

    //OPCODE TABLOLARI
    static void opcodeYukle() {

        //Komutlar
        komutOpcode.put("ATM", "00000");
        komutOpcode.put("TOP", "00001");
        komutOpcode.put("CRP", "00010");
        komutOpcode.put("CIK", "00011");
        komutOpcode.put("BOL", "00100");
        komutOpcode.put("VE",  "00101");
        komutOpcode.put("VEY", "00110");
        komutOpcode.put("D",   "00111");
        komutOpcode.put("DEG", "01000");
        komutOpcode.put("DE",  "01001");
        komutOpcode.put("DED", "01010");
        komutOpcode.put("DB",  "01011");
        komutOpcode.put("DBE", "01100");
        komutOpcode.put("DK",  "01101");
        komutOpcode.put("DKE", "01110");
        komutOpcode.put("OKU", "01111");
        komutOpcode.put("YAZ", "10000");

        //Registerlar
        operandOpcode.put("AX", "000");
        operandOpcode.put("BX", "001");
        operandOpcode.put("CX", "010");
        operandOpcode.put("DX", "011");
    }

    //Dosya Oku
    static void dosyaOku(String ad) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(ad));
        String satir;
        while ((satir = br.readLine()) != null) {
            satir = satir.trim();
            if (!satir.isEmpty())
                satirlar.add(satir);
        }
        br.close();
    }

    //Etiketleri bul
    static void etiketleriBul() {
        int adres = 0;

        for (String s : satirlar) {
            if (s.contains(":")) {
                String etiket = s.substring(0, s.indexOf(":"));
                etiketler.put(etiket, adres);
            } else {
                adres++;
            }
        }
    }

    static String dortluGrupla(String b) {

        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (i < b.length()) {
            int kalan = b.length() - i;
            int uzunluk = (kalan >= 4) ? 4 : kalan;

            sb.append(b, i, i + uzunluk);

            if (i + uzunluk < b.length())
                sb.append(" ");

            i += uzunluk;
        }

        return sb.toString();
    }

    //Operand Çevir
    static String operandBinary(String op) {

        //Register
        if (operandOpcode.containsKey(op)) {
            return operandOpcode.get(op);
        }

        //[Sabit]
        if (op.startsWith("[") && op.endsWith("]")) {
            int sayi = Integer.parseInt(op.substring(1, op.length() - 1));
            return "100" + Integer.toBinaryString(sayi);
        }

        //ETİKET
        if (op.startsWith("ETIKET")) {
            return "1111111";
        }

        //Sabit
        int sayi = Integer.parseInt(op);
        return Integer.toBinaryString(sayi);
    }

    //Binary'i Hex'e çevir
    static String binaryToHex(String b) {

        StringBuilder h = new StringBuilder();

        int i = 0;
        while (i < b.length()) {
            int kalan = b.length() - i;

            //Dörtlü dörtlü al kalanı aynen yaz
            int parcaUzunluk = (kalan >= 4) ? 4 : kalan;

            String parca = b.substring(i, i + parcaUzunluk);
            int v = Integer.parseInt(parca, 2);

            h.append(Integer.toHexString(v).toUpperCase());

            i += parcaUzunluk;
        }

        return h.toString();
    }    

    //Çevir ve Yazdır
    static void cevirVeYazdir() {

        for (String s : satirlar) {

            if (s.contains(":"))
                s = s.substring(s.indexOf(":") + 1).trim();

            if (s.isEmpty()) continue;

            String[] p = s.split("\\s+|,");

            String komut = p[0];
            StringBuilder binary = new StringBuilder();

            binary.append(komutOpcode.get(komut));

            for (int i = 1; i < p.length; i++) {
                binary.append(operandBinary(p[i]));
            }

            String hex = binaryToHex(binary.toString());
            tumHex.append(hex);

            String gruplanmisBinary = dortluGrupla(binary.toString());

                System.out.println(s + " -> " + binary + " -> " + gruplanmisBinary + " -> " + hex);
        }

        System.out.println("\nTÜM PROGRAM:");
        System.out.println(tumHex);
    }
}
