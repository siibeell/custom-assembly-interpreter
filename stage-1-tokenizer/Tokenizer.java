// Stage 1: Tokenizer
// Responsible for lexical analysis of input source code
// Converts raw text into meaningful tokens for further processing

import java.io.*; //Dosyayı okuma
import java.util.*; //Scanner,ArryList
import javax.swing.*; //Swing kısmı
import java.awt.*; //Düzen


public class Tokenizer {

    public static boolean ayniMi(String s1, String s2){
        if(s1.length() != s2.length()) return false;

        for(int i=0; i<s1.length(); i++){
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);

            if(c1 >='A' && c1 <= 'Z') c1 = (char)(c1+32);
            if(c2 >='A' && c2 <= 'Z') c2 = (char)(c2+32);

            if(c1 != c2) return false;
        }

        return true;
    }

    public static String boslukTemizle(String yazi){
        int bas = 0;
        int son = yazi.length()-1;

        //ilk boslukları atla
        while(bas < yazi.length() && yazi.charAt(bas) == ' '){
            bas++;
        }

        //son boslukları atla
        while(son >= 0 && yazi.charAt(son) == ' '){
            son--;
        }

        String temiz = "";
        for(int i=bas; i<=son; i++){
            temiz += yazi.charAt(i);
        }
        
        return temiz;
    }

    public static void main(String[] args) throws Exception{

        Scanner giris = new Scanner(System.in);
        System.out.println("HTML dosya adini giriniz: ");
        String dosyaAdi = giris.nextLine();

        FileReader dosya = new FileReader(dosyaAdi);
        BufferedReader okuyucu = new BufferedReader(dosya);

        String satir; 
        ArrayList<String> tokenlar = new ArrayList<>();

        //Satır satır okuma
        while((satir = okuyucu.readLine()) != null){
            
            String token = "";
            boolean etiketicinde = false;

            for(int i=0; i<satir.length(); i++){
                char ch = satir.charAt(i);

                if(ch == '<'){          //etiket baslangici
                    if(token.length() > 0){
                        tokenlar.add(boslukTemizle(token));
                        token = "";
                    }
                    etiketicinde = true;
                    token += ch;
                }else if(ch == '>'){            //etiket sonu
                    token += ch;
                    tokenlar.add(boslukTemizle(token));
                    token = "";
                    etiketicinde = false;
                }else{
                    token += ch;
                }
            }
            if(token.length() > 0){
                tokenlar.add(boslukTemizle(token));
            }
        }
        okuyucu.close();

        JFrame pencere = new JFrame();          //pencere olustur
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pencere.setSize(600,400);

        JPanel panel = new JPanel();            //panel olustur
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for(int i=0; i<tokenlar.size(); i++){
            String t = tokenlar.get(i);

            if(ayniMi(t, "<title>")){
                if(i+1 < tokenlar.size()){
                    pencere.setTitle(tokenlar.get(i+1));
                }
            }else if(ayniMi(t, "<h1>")){
                if(i+1 < tokenlar.size()){
                    JLabel etiket = new JLabel(tokenlar.get(i+1));
                    etiket.setFont(new Font("Arial", Font.BOLD,24));
                    panel.add(etiket);
                }
            }else if(ayniMi(t, "<h2>")){
                if(i+1 < tokenlar.size()){
                    JLabel etiket = new JLabel(tokenlar.get(i+1));
                    etiket.setFont(new Font("Arial", Font.BOLD,20));
                    panel.add(etiket);
                }
            }else if(ayniMi(t, "<h3>")){
                if(i+1 < tokenlar.size()){
                    JLabel etiket = new JLabel(tokenlar.get(i+1));
                    etiket.setFont(new Font("Arial",Font.BOLD,18));
                    panel.add(etiket);
                }
            }else if(ayniMi(t, "<h4>")){
                if(i+1 < tokenlar.size()){
                    JLabel etiket = new JLabel(tokenlar.get(i+1));
                    etiket.setFont(new Font("Arial",Font.BOLD,16));
                    panel.add(etiket);
                }
            }else if(ayniMi(t, "<h5>")){
                if(i+1 < tokenlar.size()){
                    JLabel etiket = new JLabel(tokenlar.get(i+1));
                    etiket.setFont(new Font("Arial", Font.BOLD, 14));
                    panel.add(etiket);
                }
            }else if(ayniMi(t, "<h6>")){
                if(i+1 < tokenlar.size()){
                    JLabel etiket = new JLabel(tokenlar.get(i+1));
                    etiket.setFont(new Font("Arial", Font.BOLD, 12));
                    panel.add(etiket);
                }
            }else if(ayniMi(t, "<label>")){
                if(i+1 < tokenlar.size()){
                    JLabel etiket = new JLabel(tokenlar.get(i+1));
                    panel.add(etiket);
                }
            }else if(ayniMi(t, "<input>")){
                if(i+1 < tokenlar.size()){
                    JTextField alan = new JTextField(15);
                    alan.setText(tokenlar.get(i+1));
                    panel.add(alan);
                }
            }else if(ayniMi(t, "<button>")){
                if(i+1 < tokenlar.size()){
                    JButton buton = new JButton(tokenlar.get(i+1));
                    panel.add(buton);
                }
            }else if (ayniMi(t, "<a>")) {
                if (i + 1 < tokenlar.size()) {
                    JLabel link = new JLabel("<html><u>" + tokenlar.get(i + 1) + "</u></html>");
                    link.setForeground(Color.BLUE);
                    link.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    panel.add(link);
                }
            }else if(ayniMi(t, "<p>")){
                if(i+1 < tokenlar.size()){
                    JLabel paragraf = new JLabel(tokenlar.get(i+1));
                    panel.add(paragraf);
                }
            }else if(ayniMi(t, "<span>")){
                if(i+1 < tokenlar.size()){
                    JLabel kucukYazi = new JLabel(tokenlar.get(i+1));
                    kucukYazi.setFont(new Font("Arial", Font.PLAIN, 10));
                    panel.add(kucukYazi);
                }
            }
        }
        pencere.add(panel);
        pencere.setVisible(true);
    }
}
