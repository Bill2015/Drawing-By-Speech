package talkdraw.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import talkdraw.imgobj.ImageList;
import talkdraw.imgobj.ImageObjectBuilder;
import talkdraw.imgobj.Layer;

/** <p>提供更方便的檔案讀入處理</p> 
 *  <p>繼承了 {@link FileInputStream}</p>
 *  <p>提供了以下函式做讀入動作</p>
 *  <blockquote><pre>
 *readDouble();
 *readInt();
 *readString();
 *readImage();
 *  </pre></blockquote>*/
public class FXFileInputStream extends FileInputStream{
    //===============================================================================
    /** <p>將 {@link Layer} 讀入</p> 
     *  <p>讀入順序為：  [ 圖層ID -> 名稱 -> 透明度 -> 圖層透明度 -> 旋轉 -> 翻轉 -> 長寬 -> 座標 -> 原始圖片 -> 標籤 ]
     *  @return 回傳 {@link ImageObjectBuilder} 的值 {@code [ImageObjectBuilder]}*/
    public final ImageObjectBuilder readImageObj() throws IOException{
        
        //int objID   = readInt();          //讀入物件 ID
        int layID   = readInt();            //讀入所屬圖層ID
        String name = readString();         //讀入字串 ID
        int opac    = readInt();            //讀入透明度
        int degree  = readInt();            //讀入旋轉度數
        boolean hF  = readBoolean();        //讀入水平翻轉
        boolean vF  = readBoolean();        //讀入垂直翻轉
        double iw   = readDouble();         //讀入圖片寬度
        double ih   = readDouble();         //讀入圖片高度
        double x    = readDouble();         //讀入 X 座標
        double y    = readDouble();         //讀入 Y 座標
        Image img   = readImage();        //讀入圖片

        System.out.printf( "\n------------------------\n");
        System.out.printf( "所屬圖層ID= %2d\n", layID);
        System.out.printf( "名稱= %8s\n", name);
        System.out.printf( "透明= %3d\n", opac);
        System.out.printf( "旋轉= %3d\n", degree);
        System.out.printf( "翻轉=( %5s, %5s )\n", hF, vF);
        System.out.printf( "大小=( %5s, %5s )\n", iw, ih);
        System.out.printf( "座標=( %3.1f, %3.1f )\n", x, y);
        System.out.printf( "標籤： ");

        
        ArrayList<String> tags = new ArrayList<>(); //存放標籤的 List
        //開始讀取 Image 的 Tag
        int tagSize = readInt();             //讀入 Tag 數
        for(int k = 0; k < tagSize; k++){
            String tag = readString();      //讀入 Tag
            tags.add( tag );                //加入至 List
            System.out.print( tag + " " );
        }
        System.out.println("");

        //建構出 ImageObject
        ImageObjectBuilder imgObj = new ImageObjectBuilder(name, img)
                                            .setBelongLayerID( layID )
                                            .setOpactiy( opac )
                                            .setTag( tags )
                                            .setFilp( hF, vF)
                                            .setLocation(x, y)
                                            .setImageSize(iw, ih)
                                            .setRotation( degree );

        return imgObj;
    }
    //===============================================================================
    /** <p>將 {@link Layer} 讀入</p> 
     *  <p>讀入順序為： [ ID -> 名稱 -> 透明度 -> 圖片 ]
     *  @return 回傳 double 的值 {@code [Double]}*/
    public final Layer readLayer() throws IOException{
        int ID      = readInt();      //讀入圖層ID
        String name = readString();   //讀入圖層名稱
        int opac    = readInt();      //讀入圖層Opacity
        Image image = readImage();    //讀入圖層畫布圖片

        //測試印出結果
        System.out.println( String.join(" ",    "ID=" + ID,
                                                "名稱=" + name, 
                                                "透明=" + opac ) );

        Layer layer = new Layer(ID, name, image);
        layer.setImageList( new ImageList() );
        layer.setAlpha( opac );

        return layer;
    }
    //===============================================================================
    /** <p>將 Boolean 讀入</p> 
     *  @return 回傳 Boolean 的值 {@code [Boolean]}*/
    public final boolean readBoolean() throws IOException{
        byte booleanData[] = new byte [ 1 ];                 //宣告 Boolean 資料字串長度
        read( booleanData );                                 //讀取 Boolean Byte Data
        return (booleanData[0] == 0) ? false : true;         //轉換
    }
    //===============================================================================
    /** <p>將 double 讀入</p> 
     *  @return 回傳 double 的值 {@code [Double]}*/
    public final double readDouble() throws IOException{
        byte doubleData[] = new byte [ Double.BYTES ];     //宣告 double 資料字串長度
        read( doubleData );                                //讀取 double Byte Data
        return ByteArrayToDouble( doubleData );            //轉換
    }
    //===============================================================================
    /** <p>將 int 讀入</p> 
     *  @return 回傳 int 的值 {@code [Int]}*/
    public final int readInt() throws IOException{
        byte intData[] = new byte [ Integer.BYTES ];    //宣告 int 資料字串長度
        read( intData );                                //讀取 int Byte Data
        return ByteArrayToInt( intData );               //轉換
    }
    //===============================================================================
    /**<p>將 {@link String} 讀入進來 </p>
     * <p>註：字串編碼為 {@code UTF-8} </p>
     *  @return 回傳讀入的字串 {@code [String]}*/
    public final String readString() throws IOException{
        byte [] strDataSize = new byte [ Integer.BYTES ];                 //字串長度
        read( strDataSize );                                            //讀取字串資料的長度
        byte [] strData = new byte [ ByteArrayToInt( strDataSize ) ];   //宣告字串的資料空間
        read( strData );                                                //讀取字串的資料
        return new String( strData, "UTF-8" );
    }
    //===============================================================================
     /** <p>將 JavaFx 的圖片  {@link Image}  讀出</p> 
     *  @return 回傳成功讀入的圖片 {@code (JavaFX Image)}*/
    public final Image readImage() throws IOException{
        Image img;
        byte [] sizeByte = new byte [ Integer.BYTES ];    //圖片的 Data 長度
        read( sizeByte );

        //宣告出存放 Image Data 的 Byte 陣列
        byte [] imgData = new byte [ ByteArrayToInt( sizeByte ) ];
        //讀取 Image Data
        read( imgData );       
        ByteArrayInputStream bis = new ByteArrayInputStream( imgData );
        bis.close();     //關閉 ByteArrayInputStream 蠻重要的

        //把讀出的 Image Data 轉成 BufferedImage，再轉成 JavaFx 的 Image
        img = SwingFXUtils.toFXImage( ImageIO.read( bis ) , null);

        return img;
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      轉換區(Converter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 將 Byte Array 轉成 Integer 
     *  @param b 輸入的 byte 陣列
     *  @return 回傳 轉成 Integer 的值 {@code [Int]} */
    private final int ByteArrayToInt( byte b[] ){
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order( ByteOrder.LITTLE_ENDIAN );
        return bb.getInt();
    }
    //===============================================================================
    /** 將 Byte Array 轉成 Double 
     *  @param b 輸入的 byte 陣列
     *  @return 回傳 轉成 Double 的值 */
    public double ByteArrayToDouble( byte b[] ){
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getDouble();
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      建構區(Constructor)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /**Creates a <code>FileInputStream</code> by
     * opening a connection to an actual file,
     * the file named by the path name <code>name</code>
     * in the file system.  A new <code>FileDescriptor</code>
     * object is created to represent this file
     * connection.
     * <p>
     * First, if there is a security
     * manager, its <code>checkRead</code> method
     * is called with the <code>name</code> argument
     * as its argument.
     * <p>
     * If the named file does not exist, is a directory rather than a regular
     * file, or for some other reason cannot be opened for reading then a
     * <code>FileNotFoundException</code> is thrown.
     *
     * @param      name   the system-dependent file name.
     * @exception  FileNotFoundException  if the file does not exist,
     *                   is a directory rather than a regular file,
     *                   or for some other reason cannot be opened for
     *                   reading.
     * @exception  SecurityException      if a security manager exists and its
     *               <code>checkRead</code> method denies read access
     *               to the file.
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)*/
    public FXFileInputStream(String name) throws FileNotFoundException {
        super(name);
    }

    /**Creates a <code>FileInputStream</code> by
     * opening a connection to an actual file,
     * the file named by the <code>File</code>
     * object <code>file</code> in the file system.
     * A new <code>FileDescriptor</code> object
     * is created to represent this file connection.
     * <p>
     * First, if there is a security manager,
     * its <code>checkRead</code> method  is called
     * with the path represented by the <code>file</code>
     * argument as its argument.
     * <p>
     * If the named file does not exist, is a directory rather than a regular
     * file, or for some other reason cannot be opened for reading then a
     * <code>FileNotFoundException</code> is thrown.
     *
     * @param      file   the file to be opened for reading.
     * @exception  FileNotFoundException  if the file does not exist,
     *                   is a directory rather than a regular file,
     *                   or for some other reason cannot be opened for
     *                   reading.
     * @exception  SecurityException      if a security manager exists and its
     *               <code>checkRead</code> method denies read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)*/
    public FXFileInputStream(File file) throws FileNotFoundException {
        super(file);
    }

    /**Creates a <code>FileInputStream</code> by using the file descriptor
     * <code>fdObj</code>, which represents an existing connection to an
     * actual file in the file system.
     * <p>
     * If there is a security manager, its <code>checkRead</code> method is
     * called with the file descriptor <code>fdObj</code> as its argument to
     * see if it's ok to read the file descriptor. If read access is denied
     * to the file descriptor a <code>SecurityException</code> is thrown.
     * <p>
     * If <code>fdObj</code> is null then a <code>NullPointerException</code>
     * is thrown.
     * <p>
     * This constructor does not throw an exception if <code>fdObj</code>
     * is {@link java.io.FileDescriptor#valid() invalid}.
     * However, if the methods are invoked on the resulting stream to attempt
     * I/O on the stream, an <code>IOException</code> is thrown.
     *
     * @param      fdObj   the file descriptor to be opened for reading.
     * @throws     SecurityException      if a security manager exists and its
     *                 <code>checkRead</code> method denies read access to the
     *                 file descriptor.
     * @see        SecurityManager#checkRead(java.io.FileDescriptor)*/
    public FXFileInputStream(FileDescriptor fdObj) {
        super(fdObj);
    }
}