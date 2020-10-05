package talkdraw.connection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import talkdraw.imgobj.FileImageObject;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.Layer;
import talkdraw.misc.ConsoleColors;

final public class LocalConnection {
    /** 搜尋關鍵字 */
    private String keyWord = "";
    /** 本地取得的圖片串列 */
    private HashMap<String, FileImageObject> localList = new HashMap<>();
    /** 搜尋紀錄 */
    private final HashMap<String, HashMap<String, FileImageObject>> historyMap = new HashMap<>();
    /** 一般模式用的本地圖庫 */
    private final static ArrayList<MyFile> nomal = new ArrayList<>();
    /** 人臉模式用的本地圖庫 */
    private final static ArrayList<MyFile> face = new ArrayList<>();
    /** 專業模式用的本地圖庫 */
    private final static ArrayList<MyFile> mix = new ArrayList<>();
    
    /** 現在使用的模式 */
    private USED_MODE nowMode = USED_MODE.一般;

    public static enum USED_MODE {
        人臉(face,"./人臉"), 一般(nomal, "./一般"), 混合(mix, "./");

        public ArrayList<MyFile> list;
        public String path;

        private USED_MODE(ArrayList<MyFile> list, String path) {
            this.list = list;
            this.path = path;
        }
    }

    public void setMode(USED_MODE mode) { nowMode = mode; }
    public USED_MODE getMode() { return nowMode; }
    public LocalConnection( ){
        //loadFileArray();
        
    }
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
     *  @return 搜尋到的結果數量 {@code 0 = 沒有此結果} {@code [Int]} */
    final public int setHistoryNetList( String keyword ){ 
        this.keyWord = keyword;
        localList = historyMap.get( keyword );
        return localList.size();
    }
	//---------------------------------------------------------------------------
    /** 取得 {@code 本地} 圖片物件串列 @return 網路圖片物件串列 {@code [ArrayList]} */
    public final ArrayList<FileImageObject> getLocalImageList(){ return new ArrayList<FileImageObject>( localList.values() ); }
    //===================================================================================================================
    /** 從本地取得圖片 
     *  @return 圖片數量 {@code [Int]} */
    public final int searchImages(String keyword){
        try{
            //判斷是否一樣的關鍵字
            if( keyword.equalsIgnoreCase( this.keyWord ) ){
                return localList.size();
            }
            //判斷是否之前也有搜尋過
            else if( isSearched(keyword) ){
                
                return setHistoryNetList( keyword );
            }
            else return getDoc( keyword );
        }catch(IOException e){ return 0; }
    }
    /** 取得網頁圖片資料 (這裡還未把 Tag 抓進來) 
     *  @param keyWord 要抓取圖片得關鍵字 
     *  @return 搜尋到的結果數量 [Int] */
    final public int getDoc( String keyWord ) throws IOException{
        this.keyWord = keyWord;
        /** 從本地找到的圖片串列 */
        HashMap<String, FileImageObject> localImages = new HashMap<>();
        try{
            nowMode.list.clear();
            loadFileArray(nowMode);


            int alph = 0, num = 0;
            ArrayList<MyFile> list = nowMode.list;
            for( int i = 0; i < list.size(); i++ ){
                String SN = (char)(alph + 'A') + "" + (char)(num + '0') + "";
                MyFile myFile = list.get(i);
                //FileImageObject temp = new FileImageObject( localImages.size(), SN, root, f.getName().substring(0,f.getName().lastIndexOf(".")) );

                //System.out.println( "Info：" + infoURL.get(i) );
                if(myFile.containKeys(keyWord)){
                    localImages.put( SN , new FileImageObject(localImages.size(), SN, keyWord, myFile.path, myFile.name, myFile.tagArray) );
                }


                alph += (num >= 10) ? 1 : 0;
                num = ((num + 1) % 10);
            }
            //將搜尋過的紀錄堆到歷程紀錄裡
            historyMap.put(keyWord, localImages);
        }
        catch(IndexOutOfBoundsException e){
            System.out.println( ConsoleColors.ERROR + "  =========== Get Image From Local Failture ============== ");
            e.printStackTrace();

        }catch(Exception e){
            System.out.println( ConsoleColors.ERROR + "  =========== Get Image From Local Failture ============== ");
            e.printStackTrace();
        }
        System.out.println( ConsoleColors.SUCCESS + "  =========== Get Image From Local Successful ============== ");
        
        localList = localImages;
		return localList.size();
	}
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      功能區(Function)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>取得檔案型態</p> 
     *  <p>目前有判斷的是：{@code Folder}、{@code JPG}、{@code PNG}</p>
     *  @param file 欲做判斷的檔案
     *  @return 回傳檔案型態字串 {@code [String]}*/
    private String getFileType(File file){
        try{
            //判斷是檔案 or 資料夾
            if( file.isDirectory() )return "folder";
            else{
           
                //取得最後一個 '.'
                String extend = file.getName().substring( file.getName().lastIndexOf(".") + 1 );  //取出子字串

                //判斷檔案類型
                if( extend.equalsIgnoreCase("png")  )return "pngImage";
                else if( extend.equalsIgnoreCase("jpg") )return "jpgImage";
                else return "file";
            }
        }
        catch( IndexOutOfBoundsException e){ 
            return "file"; 
        }
    }

    /** 載入本地圖片
     * @param mode 要載入的模式
     */
    private void loadFileArray(USED_MODE mode){
        switch(mode){
            case 一般:
            case 人臉:
                File file = new File( mode.path );
                for(File f : file.listFiles()){
                    //取得每個檔案的型態
                    String type = getFileType(f);
                    //判斷
                    if( type.equals("pngImage") || type.equals("jpgImage") ){
                        mode.list.add(new MyFile( f.toURI().toString(), f.getName() ));
                    }
                }
            break;
            //=========================================================================
            case 混合:
                File faceFile = new File( USED_MODE.人臉.path );
                for(File f : faceFile.listFiles()){
                    //取得每個檔案的型態
                    String type = getFileType(f);
                    //判斷
                    if( type.equals("pngImage") || type.equals("jpgImage") ){
                        mode.list.add(new MyFile( f.toURI().toString(), f.getName() ));
                    }
                }
                File normalFile = new File( USED_MODE.一般.path );
                for(File f : normalFile.listFiles()){
                    //取得每個檔案的型態
                    String type = getFileType(f);
                    //判斷
                    if( type.equals("pngImage") || type.equals("jpgImage") ){
                        mode.list.add(new MyFile( f.toURI().toString(), f.getName() ));
                    }
                }
            break;
        }
    }

    /** 儲存標籤和檔名的Class */
    class MyFile{
        //所有標籤
        ArrayList<String> tagArray = new ArrayList<>();
        //檔案路徑
        String path;
        //檔案名稱(含副檔名)
        String name;
        public MyFile(String path, String name){
            this.name = name;
            this.path = path;
            for(String tag : name.substring(0, name.lastIndexOf(".")).split("-") ){
                tagArray.add(tag);
                
            }
        }
            
        /** 是否包含所有關鍵字 */
        public boolean containKeys(String ... keywords){
            for(String key : keywords){
                if( tagArray.contains( key ) )  return true;
            }
            return false;
        }

    }

    //===================================================================================================================
    /** 把圖片放到畫布裡 (這裡處理使用 JavaScript 的回傳資料 抓取 Tags) 
     *  @param id 圖片編號
     *  @return 回傳 {@code MyImage} 物件 */
    final public ImageObject createImageObject( String ... id ){
        ArrayList<String> tagList = new ArrayList<>();
        //取出的圖片暫存
        FileImageObject image = null;

        //假如有填入 ID 就判斷一下
        if( id.length >= 1 && id[0] != null ){
            image = localList.get( id[0] );
        }
        else{
            image = new ArrayList<>( localList.values() ).get( (int)(localList.size() * Math.random()) );
        }

        if( image == null )return null;
        
        for( String str : tagList ){
            System.out.println( "tags : " + str );
        }
        //所屬圖層之後才會逕行設定
        return new ImageObject(Layer.NONE, image.getKeyword() , image.getImage(), image.getTags() );
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
        ImageObject imgObj = this.createImageObject( id );
        if( imgObj == null )return null;
        imgObj.setLocation(x, y);
        imgObj.filp( filpType );
        imgObj.setAlpha( opacity );
        imgObj.setRotation( rotation );
        imgObj.setImageSize(width, height);
        return imgObj;
    }

}
