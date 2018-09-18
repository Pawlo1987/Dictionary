package com.example.user.dictionary;

//-----Объект для упаковки данных-----
public class Word {
    private String idWord;      //id слова
    private String strRus;      //строка с русским словом
    private String strHeb;      //строка с ивритовским словом
    private String strTrans;    //строка с транскрпцией
    private String genRus;   //строка для рода в русском
    private String genHeb;   //строка для рода в иврите
    private String quantity;    //строка множест. или единств. число
    private String meaning;     //строка значения слова в предложении

    public String getIdWord() { return idWord; }
    public void setIdWord(String idWord) { this.idWord = idWord; }
    public String getStrRus() { return strRus; }
    public void setStrRus(String strRus) { this.strRus = strRus; }
    public String getStrHeb() { return strHeb; }
    public void setStrHeb(String strHeb) { this.strHeb = strHeb; }
    public String getStrTrans() { return strTrans; }
    public void setStrTrans(String strTrans) { this.strTrans = strTrans; }
    public String getGenRus() { return genRus; }
    public void setGenRus(String genRus) { this.genRus = genRus; }
    public String getGenHeb() { return genHeb; }
    public void setGenHeb(String genHeb) { this.genHeb = genHeb; }
    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
}//Word
