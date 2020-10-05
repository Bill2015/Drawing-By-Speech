package talkdraw.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.Layer;
import talkdraw.imgobj.NetImageObject;
import talkdraw.misc.ConsoleColors;

//javac -classpath C:\TalkDraw\jsoup-1.11.3.jar; *.java
//java -classpath C:\TalkDraw\jsoup-1.11.3.jar; NetConnection
final public class NetConnection {
    /** 網頁的主要網域 */
    private final String PNG_WEB_SITE = "https://icons8.com";
    /** 搜尋關鍵字 */
    private String keyWord = "";

    final private boolean useCookies = true;

    private final int SIZE_OF_GETTING_PICTURE = 10;

    private final HashMap<String, HashMap<String, NetImageObject>> historyMap;

    /** 網路取得的圖片串列 */
    private HashMap<String, NetImageObject> netList = new HashMap<>();
    /**============================================ */
    /** Selenium 的 Cookies 網站暫存 */
    private Set<Cookie> cookies;
    /** Chrome 網站設定 */
    private ChromeOptions options;
    private WebDriver driver;
    /**============================================ */
    /** Jsoup 的 Cookies 設定 */
    private Connection.Response jsoupResponse;
    private Map<String, String> jsoupCookies;
    public NetConnection( ){
        System.out.println( ConsoleColors.INFO + "Starting initializing [Selenium Driver]......");				
        options = new ChromeOptions();
        options.addArguments("--headless"); //無遊覽器模式
        options.addArguments("start-maximized"); // https://stackoverflow.com/a/26283818/1689770
        options.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770
        options.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
        options.addArguments("--disable-infobars"); //https://stackoverflow.com/a/43840128/1689770
        options.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/50725918/1689770
        options.addArguments("--disable-browser-side-navigation"); //https://stackoverflow.com/a/49123152/1689770
        options.addArguments("--disable-gpu"); //https://stackoverflow.com/questions/51959986/how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc
        System.setProperty("webdriver.chrome.driver", getClass().getResource("/chromedriver/chromedriver.exe").getFile() );
        System.out.println( ConsoleColors.SUCCESS + "Initialize [Selenium Driver] successful\n");				

        historyMap = new HashMap<>();

        if( useCookies == true ){
            driver = new ChromeDriver(options);
            initialSelemiumCookies();
            initialJsoupCookies();
        }
    }
    //===================================================================================================================
    /** 取得 {@code 網路} 圖片物件串列 @return 網路圖片物件串列 {@code [ArrayList]} */
    public final ArrayList<NetImageObject> getNetImageList(){ return new ArrayList<NetImageObject>( netList.values() ); }
    //===================================================================================================================
    /** 取得目前儲存的 {@code 關鍵字[Keyword]} @return 關鍵字 {@code [String]} */
    final public String getNowKeyword(){ return keyWord; }
    //===================================================================================================================
    /** 取得目前是否有搜尋過此關鍵字的 
     *  @param keyword {@code 關鍵字[Keyword]}
     *  @return {@code true = 有搜尋過} | {@code false = 沒有搜尋過} */
    final public boolean isSearched( String keyword ){ return historyMap.containsKey( keyword ); }
    //===================================================================================================================
    /** 取得此關鍵字搜尋出來的 {@code NetImage}
     *  @param keyword {@code 關鍵字[Keyword]}
     *  @return 搜尋到的結果數量 {@code 0 = 沒有此結果}  {@code [Int]} */
    final public int setHistoryNetList( String keyword ){
        this.keyWord = keyword; 
        netList = historyMap.get( keyword );
        return netList.size();
    }
    //===================================================================================================================
    /** 從網路取得圖片 
     *  @return 圖片數量 {@code [Int]} */
    final public int searchImages(String keyword){
        try{
            //判斷是否一樣的關鍵字
            if( keyword.equalsIgnoreCase( this.keyWord ) ){
                return netList.size();
            }
            //判斷是否之前也有搜尋過
            else if( isSearched( keyword ) ){
                return setHistoryNetList( keyword ) ;
            }
            else return getDoc( keyword );
        }catch(IOException e){ return 0; }
    }
    //===================================================================================================================
    /** 取得網頁圖片資料 (這裡還未把 Tag 抓進來) 
     *  @param keyWord 要抓取圖片得關鍵字 
     *  @return 搜尋到的結果數量 [Int] */
    final public int getDoc( String keyWord ) throws IOException{
        this.keyWord = keyWord;
        /** 從網路撈回來的圖片串列 */
        HashMap<String, NetImageObject> netImages = new HashMap<>();
        ArrayList<String> imgURL = new ArrayList<>();
        ArrayList<String> infoURL = new ArrayList<>();
        System.out.println("  =============== Start Get Image From WebSite =============== ");
        String s = "https://icons8.com/icons/set/" + this.keyWord;

        Document doc;
        if( useCookies == true )
            doc = Jsoup.connect( s ).cookies( jsoupCookies ).get();
        else 
            doc = Jsoup.connect( s ).get();
        //--------------------------------------------------------------------
        //取得圖片的展示圖
        Elements icons = doc.select("img[alt][src][style]");
        for( Element icon : icons ){
            imgURL.add( icon.attr("src") );
        }
        //--------------------------------------------------------------------
        //取的圖片的詳細網址
        Elements infos = doc.select("a[href^=/icon/][draggable=false][class=icon-link]");
        for( Element icon : infos ){
            infoURL.add( PNG_WEB_SITE + icon.attr("href") );
        }
        //--------------------------------------------------------------------
        //合併且新增物件
        try{
            int alph = 0, num = 0;
            for( int i = 0; i < imgURL.size() && i < SIZE_OF_GETTING_PICTURE; i++ ){
                String SN = (char)(alph + 'A') + "" + (char)(num + '0') + "";
                
                System.out.println( "Info：" + infoURL.get(i) );
                netImages.put( SN ,new NetImageObject( netImages.size(), SN, imgURL.get(i), infoURL.get(i), keyWord) );

                alph += (num >= 10) ? 1 : 0;
                num = ((num + 1) % 10);
            }

            //將搜尋過的紀錄堆到歷程紀錄裡
            historyMap.put(keyWord, netImages);
        }
        catch(IndexOutOfBoundsException e){
            System.out.println( ConsoleColors.ERROR + "  =========== Get Image From WebSite Failture ============== ");
        }
        System.out.println( ConsoleColors.INFO + "  =========== Get Image From WebSite Successful ============== ");
        netList = netImages;
        return netList.size();
    }
    //===================================================================================================================
    /** 把圖片放到畫布裡 (這裡處理使用 JavaScript 的回傳資料 抓取 Tags) 
     *  @param id 圖片編號
     *  @return 回傳 {@code MyImage} 物件 */
    public ImageObject createImageObject( String ... id ){
        //取出的圖片暫存
        NetImageObject netImage = null;

        //假如有填入 ID 就判斷一下
        if( id.length >= 1 && id[0] != null )
            netImage = netList.get( id[0] );

        //假如沒有填入 ID 就是隨機選取一張
        else
            netImage = new ArrayList<>( netList.values() ).get( (int)(netList.size() * Math.random()) );

        if( netImage == null )return null;
        //用 Htmlunit 來抓取 JavaScript 執行過的文本
        Document doc = getHtmlDoc( netImage.getImageInfoURL() );

        //所屬圖層之後才會逕行設定
        return new ImageObject( Layer.NONE, netImage.getKeyword() , netImage.getImage(), getWebTags( doc )); 
    }
    /** 把圖片放到畫布裡 (這裡處理使用 JavaScript 的回傳資料 抓取 Tags) 
     *  @param id 圖片編號
     *  @param x 座標 X
     *  @param y 座標 Y
     *  @param filpType 翻轉類型
     *  @param opacity 透明度
     *  @param rotation 旋轉度數
     *  @param width 圖片寬度
     *  @param height 圖片高度
     *  @return 回傳 {@code MyImage} 物件 */
    public ImageObject createImageObject(  String id, double x, double y, short filpType, int opacity, int rotation, double width, double height ){
        ImageObject imgObj = createImageObject( id );
        if( imgObj == null )return null;
        imgObj.setLocation(x, y);
        imgObj.filp( filpType );
        imgObj.setAlpha( opacity );
        imgObj.setRotation( rotation );
        imgObj.setImageSize(width, height);
        return imgObj;
    }
    //================================================================================================
    /** 利用 htmlunit 來模擬無介面遊覽器 以方便 JavaScript 的執行 
     *  @param str 圖片的詳細網頁
     *  @return 將 JavaScript 執行完的結果輸出成 {@code Html Document} 回傳*/
    final private Document getHtmlDoc( String str )  {
        if( driver == null )
            driver = new ChromeDriver(options);
       /* for( Cookie cookie : cookies )
            driver.manage().addCookie( cookie );*/
        //等待時間 10 秒
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30L, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(30L, TimeUnit.SECONDS);
        //造訪網站
        driver.get( str );
        System.out.println( "===================網頁資料 Tag ====================" ); 
        // 取得回傳回來的 Html File
        String htmlFile = driver.getPageSource();
        //取得網站 Cookies
       // cookies.addAll( driver.manage().getCookies() );

        return Jsoup.parse( htmlFile );
    }
    //================================================================================================
    /** 關閉 Browser Driver */
    final public void closeDriver(){
        if( driver == null )return;
        try{	  	
            File file = new File( getClass().getResource("/settings/Cookies.data").getFile() );
        
            file.deleteOnExit();
            FileWriter fileWrite = new FileWriter( file );							
            BufferedWriter Bwrite = new BufferedWriter( fileWrite );							            	
            // loop for getting the cookie information 		
            for(Cookie ck : driver.manage().getCookies()){			
                Bwrite.write( (ck.getName()  + ";" +
                              ck.getValue()  + ";" +
                              ck.getDomain() + ";" +
                              ck.getPath()   + ";" +
                              ck.getExpiry() + ";" +
                              ck.isSecure() ) );																									
                Bwrite.newLine();             
            }			
            Bwrite.close();	
        }
        catch(Exception ex){		
            ex.printStackTrace();			
        }	
        driver.close();
        driver.quit();	
    }
    //================================================================================================
    /** 取得網路標籤 
     *  @param doc 要分析的 {@link Document}
     *  @return 標籤串列 {@code [ArrayList<String>]}*/
    final public ArrayList<String> getWebTags( Document doc ){
        ArrayList<String> tagList = new ArrayList<>();

        //取得第一行 Tags
        String tag1 = doc.select("a.is-text.link-category").get(0).text(); 
        tag1 = tag1.substring(0, tag1.length() - 2);
        tagList.addAll( Arrays.asList( tag1 ) );

        //取得第二行 Tags
        String tags[] = doc.select("a.is-text.link-category").get(1).text().split(" → "); 
        tagList.addAll( Arrays.asList( tags ) );

        //取得第三行 Tags
        String tag3 = doc.select("a.is-text.link-title").first().text();
        tagList.add( tag3.substring(2, tag3.length() ) );
        //印出有哪些標籤
        for( String line : new ArrayList<>(tagList) ){
            for( String str : line.split(" ") ){
                if( !tagList.contains(str) && !str.equalsIgnoreCase("of") && !str.equals("&") )tagList.add( str );
            }
        }

        for( String str : tagList ){
            System.out.println( "tags : " + str );
        }
        return tagList;
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡     初始化 Cookies    ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 初始化 Jsoup 的 Cookies */
    private void initialJsoupCookies(){
        try{
            System.out.println( ConsoleColors.INFO + "Starting initializing [Jsoup Cookies]......");				
            jsoupResponse = Jsoup.connect( PNG_WEB_SITE ).execute();
            jsoupCookies = jsoupResponse.cookies();
            System.out.println( ConsoleColors.SUCCESS + "Initialize [Jsoup Cookies] successful\n");				
        }
        catch( Exception e){
            System.out.println( ConsoleColors.ERROR + "Initialize [Jsoup Cookies] Ocur an error");				
        }
    }
    /** 初始化 Selemium 的 Cookies */
    private void initialSelemiumCookies(){
        System.out.println( ConsoleColors.INFO + "Starting initializing [Selenium Cookies]......");				
        try{			
            //先造訪網站
            driver.get( PNG_WEB_SITE );
            cookies = driver.manage().getCookies();

            //開啟 Cookies 設定檔
            File file = new File( getClass().getResource("/settings/Cookies.data").getFile() );							
            FileReader fileReader = new FileReader( file );							
            BufferedReader Buffreader = new BufferedReader( fileReader );							
            String strline;			
            while( (strline = Buffreader.readLine() ) != null ){									
                StringTokenizer token = new StringTokenizer(strline, ";");									
                while( token.hasMoreTokens() ){					
                    String name     = token.nextToken();	    //名稱				
                    String value    = token.nextToken();	    //值		
                    String domain   = token.nextToken();	    //網域		
                    String path     = token.nextToken();		//路徑		
                    Date expiry     = null;					    //日期
                            
                    String val;			
                    if(!( val = token.nextToken() ).equals("null")){		
                        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                      //  System.out.println( ConsoleColors.ACTION + df.format( new Date() ) );
                        expiry = df.parse( val );					
                    }		
                    Boolean isSecure = Boolean.parseBoolean( token.nextToken() );								
                    Cookie ck = new Cookie(name, value, domain, path, expiry, isSecure);			
                    System.out.println( ConsoleColors.INFO + "Cookies： " + ck);
                    driver.manage().addCookie( ck ); // This will add the stored cookie to your current session					
                }		
            }
            Buffreader.close();	
            System.out.println( ConsoleColors.SUCCESS + "Initialize [Selenium Cookies] successful\n");					
        }
        catch(FileNotFoundException e){
            System.out.println( ConsoleColors.WARNING + "Dosen't have [Selenium Cookie] file");
        }
        catch(Exception ex){	
            System.out.println( ConsoleColors.ERROR + "Initializing [Selenium Cookies] Ocur an error");				
            ex.printStackTrace();			
        }
    }
}
