package com;
/**
 * Created by milen_000 on 2/14/2018.
 */
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scrubber {

    Set<Link> links;

    public Scrubber(){
        links = new HashSet<>();
    }

    public void getLinksFromUrl(String urlAddress){

        String pageContent = "";
        try {
            pageContent = getTextFromHtml(urlAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(pageContent.isEmpty()) return;

        // we are only interested in the href=""
        Pattern linkPattern = Pattern.compile("<a[^>]+href=[\"']?([^\"']+)[\"']?[^>]*>(.+?)</a>",  Pattern.CASE_INSENSITIVE| Pattern.DOTALL);
        Matcher pageMatcher = linkPattern.matcher(pageContent);

        // HashSet contains unique elements only.
        while(pageMatcher.find()){
            Link next = new Link(pageMatcher.group(1));
            next.setValid();
            links.add(next);      //  access the link part
        }
    }

    public boolean saveToFile(String filename) {
        if(filename.isEmpty())
            return false;
        if(links.isEmpty())
            return false;
        // When serializing an object to a file, the standard convention in Java is to give the file a .ser extension.
        if(!filename.contains(".ser"))
            filename += ".ser";
      try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
            oos.writeObject(links);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean loadFromFile(String filename){
        if(filename.isEmpty())
            return false;
        if(!filename.contains(".ser"))
            filename += ".ser";

        File f = new File(filename);
        if(!f.exists()){
            System.out.println("File doesn't exist");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))){

            links = (HashSet<Link>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void printLinks(){
        if(links.isEmpty()){
            System.out.println("There is no links");
            return;
        }
        Iterator<Link> it = links.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
    }

    public String getTextFromHtml(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    public static void main(String[] args) {
        // args (or command line) contains url address and filename to store result
        // if no arguments - take default
        String htmlpage;
        String filename;

        if(args.length == 2){
             htmlpage = args[0];
             filename = args[1];
        }
        else{
              htmlpage =  "https://en.wikipedia.org/wiki/Purim";
              filename = "/temp/pagelinks.ser";
        }

        Scrubber myScrubber = new Scrubber();
        myScrubber.getLinksFromUrl(htmlpage);
        myScrubber.saveToFile(filename);
        myScrubber.loadFromFile(filename);
        myScrubber.printLinks();
    }}

