package talkdraw.io; 

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.Layer;

import java.awt.image.BufferedImage;

/** <p>提供更方便的檔案寫入入處理</p> 
 *  <p>繼承了 {@link FileOutputStream}</p>
 *  <p>提供了以下函式做寫入動作</p>
 *  <blockquote><pre>
 *writeDobule();
 *writeInt();
 *writeString();
 *writeImage();
 *  </pre></blockquote>*/
public class FXFileOutputStream extends FileOutputStream{
    /** <p>將 {@link ImageObject}  寫入至檔案</p>
     *  <p>寫入順序為： [ 圖層ID -> 名稱 -> 透明度 -> 旋轉 -> 翻轉 -> 圖片大小 -> 座標 -> 原始圖片 -> 標籤 ]</p>
     *  <ul>
     *  <li>１. 所屬圖層ID</li>
     *  <li>２. 名稱</li>
     *  <li>３. 透明度</li>
     *  <li>４. 旋轉</li>
     *  <li>５. 水平翻轉</li>
     *  <li>６. 垂直翻轉</li>
     *  <li>７. 圖片寬度</li>
     *  <li>８. 團片高度</li>
     *  <li>９. X 座標</li>
     *  <li>１０. Y 座標</li>
     *  <li>１１. 原始圖片</li>
     *  <li>１２. 標籤s</li>
     *  </u>
     *  @param imgObj 欲寫入的圖片物件 */
    public void writeImageObj( ImageObject imgObj ) throws IOException{
        //writeInt( imgObj.getID() );                   //寫入ID
        writeInt( imgObj.getBelongLayer().getID() );    //寫入所屬圖層 ID
        writeString( imgObj.getName() );                //寫入名稱
        writeInt( imgObj.getAlpha() );                  //寫入透明度
        writeInt( imgObj.getRotation() );               //寫入目前選轉度數
        writeBoolean( imgObj.getFilp()[0] );            //寫入水平翻轉
        writeBoolean( imgObj.getFilp()[1] );            //寫入垂直翻轉
        writeDobule( imgObj.getImageWidth() );          //寫入 圖片 寬度
        writeDobule( imgObj.getImageHeight() );         //寫入 團片 高度
        writeDobule( imgObj.getX() );                   //寫入 X 做標
        writeDobule( imgObj.getY() );                   //寫入 Y 座標
        writeImage( imgObj.getImage() );                //寫入原始圖片

        //取出每一個圖片標籤 並 寫入圖片標籤
        writeInt( imgObj.getTags().size() );  //寫入標籤數量
        for(String tag : imgObj.getTags() ){        
            writeString( tag );                   //寫入標籤
        }
    }
    //===============================================================================
    /** <p>將 {@link Layer}  寫入至檔案</p>
     *  <p>寫入順序為： [ ID -> 名稱 -> 透明度 -> 圖片 ]</p>
     *  @param layer 欲寫入的圖層 */
    public void writeLayer( Layer layer ) throws IOException{
        writeInt(     layer.getID()   );          //寫入ID
        writeString(  layer.getName() );          //寫入名稱
        writeInt(     layer.getAlpha() );         //寫入透明度
        writeCavans(  layer.getCanvas() );        //將畫布寫入
    }
    //===============================================================================
    /** 將 {@link Cavans} 轉成 {@link Image} 在寫入至檔案
     *  @param canvas 欲寫入的畫布 */
    public void writeCavans(Canvas canvas) throws IOException{
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill( Color.TRANSPARENT );
        writeImage( canvas.snapshot( parameters , null ) );
    }
    //===============================================================================
    /** <p>將 Boolean 寫入至檔案</p> 
     *  @param value 欲寫入的 Boolean*/
    public final void writeBoolean(boolean value) throws IOException{
        write( new byte[]{ (byte) (value ? 1 : 0 ) }  );       //將 Boolean 轉換 Byte 後寫入
    }
    //===============================================================================
    /** <p>將 double 寫入至檔案</p> 
     *  @param value 欲寫入的 double*/
    public final void writeDobule(double value) throws IOException{
        write( DoubleToByteArray( value ) );       //將 double 轉換 Byte 後寫入
    }
    //===============================================================================
    /** <p>將 int 寫入至檔案</p> 
     *  @param value 欲寫入的 int*/
    public final void writeInt(int value) throws IOException{
        write( IntToByteArray( value ) );       //將 int 轉換 Byte 後寫入
    }
    //===============================================================================
    /** <p>將 {@link String} 寫入至檔案裡 </p>
     *  <p>註：字串編碼為 {@code UTF-8} </p>
     *  @param str 欲寫入的字串 {@link String}*/
    public final void writeString( String str ) throws IOException{
        byte [] strByte = str.getBytes( Charset.forName("UTF-8") ); //計算字串的長度
        write( IntToByteArray( strByte.length ) );                  //寫入字串長度
        write( strByte );                                           //寫入字串 Data
    }
    //===============================================================================
    /** <p>將 JavaFx 的圖片 {@link Image} 寫入至檔案</p> 
     *  @param img 欲寫入的圖片 {@code (JavaFX Image)}*/
    public final void writeImage( Image img ) throws IOException{
        //將 FX Image 轉成 BufferedImage
        BufferedImage bufferImage = SwingFXUtils.fromFXImage( img, null);    

        //將圖片轉成 Byte Array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferImage, "png", baos );
        byte[] byteImgData = baos.toByteArray();
        baos.close();       //關閉 ByteArrayOutputStream 蠻重要的

        //先寫入這個圖片的 Byte 數，再寫入圖片的資料
        write( IntToByteArray( byteImgData.length ) );
        write( byteImgData );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      轉換區(Converter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 將 Integer 轉成 Byte Array 
     *  @param i 輸入的 Integer
     *  @return 回傳 轉成 byte Array 的值 {@code [byte []]}  */
    private final byte [] IntToByteArray( int i ){
        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        bb.order( ByteOrder.LITTLE_ENDIAN );
        bb.putInt(i);
        return bb.array();
    }
    //===================================================================
    /** 將 Double 轉成 Byte Array 
     *  @param i 輸入的 Double
     *  @return 回傳 轉成 byte Array 的值 */
    public byte [] DoubleToByteArray( double i ){
        final ByteBuffer bb = ByteBuffer.allocate(Double.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putDouble(i);
        return bb.array();
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      建構區(Constructor)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /**Creates a file output stream to write to the file with the
     * specified name. A new <code>FileDescriptor</code> object is
     * created to represent this file connection.
     * <p>
     * First, if there is a security manager, its <code>checkWrite</code>
     * method is called with <code>name</code> as its argument.
     * <p>
     * If the file exists but is a directory rather than a regular file, does
     * not exist but cannot be created, or cannot be opened for any other
     * reason then a <code>FileNotFoundException</code> is thrown.
     *
     * @implSpec Invoking this constructor with the parameter {@code name} is
     * equivalent to invoking {@link #FileOutputStream(String,boolean)
     * new FileOutputStream(name, false)}.
     *
     * @param      name   the system-dependent filename
     * @exception  FileNotFoundException  if the file exists but is a directory
     *                   rather than a regular file, does not exist but cannot
     *                   be created, or cannot be opened for any other reason
     * @exception  SecurityException  if a security manager exists and its
     *               <code>checkWrite</code> method denies write access
     *               to the file.
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)*/
    public FXFileOutputStream(String name) throws FileNotFoundException {
        super(name);
    }
    /**Creates a file output stream to write to the file represented by
     * the specified <code>File</code> object. A new
     * <code>FileDescriptor</code> object is created to represent this
     * file connection.
     * <p>
     * First, if there is a security manager, its <code>checkWrite</code>
     * method is called with the path represented by the <code>file</code>
     * argument as its argument.
     * <p>
     * If the file exists but is a directory rather than a regular file, does
     * not exist but cannot be created, or cannot be opened for any other
     * reason then a <code>FileNotFoundException</code> is thrown.
     *
     * @param      file               the file to be opened for writing.
     * @exception  FileNotFoundException  if the file exists but is a directory
     *                   rather than a regular file, does not exist but cannot
     *                   be created, or cannot be opened for any other reason
     * @exception  SecurityException  if a security manager exists and its
     *               <code>checkWrite</code> method denies write access
     *               to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)*/
    public FXFileOutputStream( File file ) throws FileNotFoundException, SecurityException{
        super(file);
    }
    /*** Creates a file output stream to write to the file with the specified
     * name.  If the second argument is <code>true</code>, then
     * bytes will be written to the end of the file rather than the beginning.
     * A new <code>FileDescriptor</code> object is created to represent this
     * file connection.
     * <p>
     * First, if there is a security manager, its <code>checkWrite</code>
     * method is called with <code>name</code> as its argument.
     * <p>
     * If the file exists but is a directory rather than a regular file, does
     * not exist but cannot be created, or cannot be opened for any other
     * reason then a <code>FileNotFoundException</code> is thrown.
     *
     * @param     name        the system-dependent file name
     * @param     append      if <code>true</code>, then bytes will be written
     *                   to the end of the file rather than the beginning
     * @exception  FileNotFoundException  if the file exists but is a directory
     *                   rather than a regular file, does not exist but cannot
     *                   be created, or cannot be opened for any other reason.
     * @exception  SecurityException  if a security manager exists and its
     *               <code>checkWrite</code> method denies write access
     *               to the file.
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @since     1.1*/
    public FXFileOutputStream(String name, boolean append) throws FileNotFoundException{
        super(name, append);
    }
    /** Creates a file output stream to write to the specified file
     * descriptor, which represents an existing connection to an actual
     * file in the file system.
     * <p>
     * First, if there is a security manager, its <code>checkWrite</code>
     * method is called with the file descriptor <code>fdObj</code>
     * argument as its argument.
     * <p>
     * If <code>fdObj</code> is null then a <code>NullPointerException</code>
     * is thrown.
     * <p>
     * This constructor does not throw an exception if <code>fdObj</code>
     * is {@link java.io.FileDescriptor#valid() invalid}.
     * However, if the methods are invoked on the resulting stream to attempt
     * I/O on the stream, an <code>IOException</code> is thrown.
     *
     * @param      fdObj   the file descriptor to be opened for writing
     * @exception  SecurityException  if a security manager exists and its
     *               <code>checkWrite</code> method denies
     *               write access to the file descriptor
     * @see        java.lang.SecurityManager#checkWrite(java.io.FileDescriptor)*/
    public FXFileOutputStream(FileDescriptor fdObj) {
        super( fdObj );
    }
}