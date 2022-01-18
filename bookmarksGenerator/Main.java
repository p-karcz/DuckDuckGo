import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main {

    public static void main(String[] args) {
        try{
            int numberOfBookmarks = 30000;
            FileWriter fstream = new FileWriter("duckduckgobookmarks" + numberOfBookmarks + ".html");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n" +
                    "<!--This is an automatically generated file.\n" +
                    "It will be read and overwritten.\n" +
                    "Do Not Edit! -->\n" +
                    "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n" +
                    "<Title>Bookmarks</Title>\n" +
                    "<H1>Bookmarks</H1>\n" +
                    "<DL><p>\n" +
                    "    <DT><H3 ADD_DATE=\"1618844074\" LAST_MODIFIED=\"1618844074\" PERSONAL_TOOLBAR_FOLDER=\"true\">DuckDuckGo Bookmarks</H3>\n" +
                    "    <DL><p>");

            for(int i=1; i< numberOfBookmarks; i++){
                out.write("        <DT><A HREF=\"https://myanimelist.net/manga/" + i);
                out.write("/\" ADD_DATE=\"1618844074\" LAST_MODIFIED=\"1618844074\">Manga number " + i + "</A>\n");
            }

            out.write("    </DL><p>\n" +
                    "</DL><p>");



            out.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}
