package com.example.rianthai.model;
import java.util.ArrayList;

public class Translation {
    String search,word;
    ArrayList<String> meaningArr,suggestionArr;

    public Translation(String search){
        this.search = search;
        meaningArr = new ArrayList<>(); //To store matched results
        suggestionArr = new ArrayList<>(); //To store word suggestions
    }

    public void setMeaningArr(String content){
        meaningArr.add(content);
    }

    public ArrayList<String> getMeaningArr() {
        return meaningArr;
    }

    public ArrayList<String> getSuggestionArr() {
        return suggestionArr;
    }

    public void setSuggestionArr(String content){
        suggestionArr.add(content);
    }

    public void setWord(String word){
        this.word = word;
    }

    public boolean meaningChecker(String content) { //using parser to specify a dictionary
        if(word.equals(search)) {
            if (mainDictionary(content)) {
                return true;
            } else return !content.contains("ˈ") && content.contains("(n)") || content.contains("(int)")
                    || content.contains("(vt)") || content.contains("(adj)")
                    || content.contains("(adv)") || content.contains("(pre)")
                    || content.contains("(pro)") || content.contains("(pron)")
                    || content.contains("(conj)") || content.contains("(prf)")
                    || content.contains("(vi)");
        }
        return false;
    }

    public boolean suggestionChecker(String content) {
        if(word.contains(search))
            if(mainDictionary(content)){
                for (int i = 0; i < word.length() - 1; i++) { //To avoid English character result
                    char c = word.charAt(i);
                    if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')))
                        return false;
                }
                return true;
            }
        return false;
    }

    public boolean mainDictionary(String content) {
        return !content.contains("ˈ") && content.contains("[N]") || content.contains("[INT]")
                || content.contains("[VT]") || content.contains("[ADJ]")
                || content.contains("[ADV]") || content.contains("[PRE]")
                || content.contains("[PRO]") || content.contains("[PRON]")
                || content.contains("[CONJ]") || content.contains("[PRF]")
                || content.contains("[VI]");
    }
}
