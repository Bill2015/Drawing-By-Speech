package talkdraw.tools;

import java.util.HashMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import talkdraw.imgobj.Layer;

/**<p>工具的母親，基本上要寫什麼工具可以繼承他</p> 
 *<p>注意：他是 {@code 抽象類別} 所以不能拿來宣告物件</p> 
 *<p>在 {@link talkdraw.MainDrawPane } 裡使用 {@code new} 的方式</p> 
 *<blockquote><pre>
 *Tool tool = new PencilTool();
 *Tool tool = new RectangleTool();
 *  if(tool instanceof PencilTool) //可以判斷是否是這個物件
 *  </pre></blockquote></p>
 *  @see PencilTool 
 *  @see RectangleTool */
abstract public class Tool extends ToggleButton{
    /** 語音繪畫確定 */
    public boolean checkDraw = false;
    /** 動作中的layer */
    protected Layer activeLayer;
    /** 畫筆 */
    protected GraphicsContext mainGC;
    /** 預覽用畫筆 */
    protected GraphicsContext prevGC;
    /**  <p>畫筆的顏色</p><p> {@code inSide} 是指內部 </p><p> {@code outSide} 是指邊框 </p>  */
    protected Color inSide, outSide;
    /** 工具的粗細 */
    protected int penSize;
    /** 工具的屬性面板 */
    protected Pane detailPane;
    private final int ID;
    private static int COUNTER;

    /** 建構子
     *  @param name 工具名稱 */
    public Tool(String name){ 
        super(name);
        ID = COUNTER++;
    }
    /** 建構子
     *  @param name 工具名稱 
     *  @param mainGC 主要畫布畫筆
     *  @param prevGC 預覽畫布畫筆*/
    public Tool(String name, GraphicsContext mainGC, GraphicsContext prevGC){
        this( name );
        this.mainGC = mainGC;
        this.prevGC = prevGC;
    }
    //----------------------------------------------------------------------------
    /** 滑鼠事件 */
    abstract public void onMouseClick( MouseEvent e );
    abstract public void onMouseRelease( MouseEvent e );
    abstract public void onMouseDragging( MouseEvent e );
    abstract public void onMouseMove( MouseEvent e );
    //-----------------------------------------------------------------------------
    /** 語音控制 */
    /** 畫出預覽
     * @param pos 所有參數
     * @return 提示字串或運行狀態
     * @throws Exception
     */
    abstract public String speechDraw(String ... poss) throws Exception;
    /** 設定屬性
     * @param name 屬性名稱
     * @param value 要改的數值
     * @return 提示字串或運行狀態
     * @throws Exception 
     */
    abstract public String setValue(String name, String value) throws Exception;
    //-----------------------------------------------------------------------------
    /** 更新工具細節 */
    abstract public void updataTool( );
    /** 初始化 detailPane */
    abstract protected void initialPane( );
    /** 初始化 detailPane 的監聽器*/
    abstract protected void initialListener( );
    //--------------------------------------------------------------------
    /** 設定目前的畫布畫筆
     *  @param gc 欲設定的 畫筆 {@code [GraphicsContext]}  */
    final public void setMainGC(Layer layer){ 
        activeLayer = layer;
        mainGC = activeLayer.getCanvas().getGraphicsContext2D();
    }
    //--------------------------------------------------------------------
    /** 設定目前的預覽畫筆
     *  @param gc 欲設定的 畫筆 {@code [GraphicsContext]}  */
    final public void setPrevGC(GraphicsContext gc){ prevGC = gc; }
     //--------------------------------------------------------------------
    /** <p>設定工具顏色</p> 
     *  <p>假如要增加設定細節的元件，記得一定要去 {@link talkdraw.DetailPane#getChildrenNode()} 那裡增加回傳值</p>
     *  @param inSide 內部顏色
     *  @param outSide 外部顏色 
     *  @see tool {@link talkdraw.DetailPane#getChildrenNode()}</p>*/
     public void setColor( Color inSide, Color outSide ){
        //this.inSide = inSide;
        //this.outSide = outSide;

        setInSideColor(inSide);
        setOutSideColor(outSide);
        
    };
    //--------------------------------------------------------------------
    /** <p>設定工具顏色</p> 
     *  <p>假如要增加設定細節的元件，記得一定要去 {@link talkdraw.DetailPane#getChildrenNode()} 那裡增加回傳值</p>
     *  <p>------------------------------------</p>
     *  @param inSide 內部顏色
     *  @see tool {@link talkdraw.DetailPane#getChildrenNode()}</p>*/
    public void setInSideColor( Color inSide ){
        this.inSide = inSide;

        mainGC.setFill(inSide);
        prevGC.setFill(inSide);
        
    };
    //--------------------------------------------------------------------
    /** <p>設定工具顏色</p> 
     *  <p>假如要增加設定細節的元件，記得一定要去 {@link talkdraw.DetailPane#getChildrenNode()} 那裡增加回傳值</p>
     *  <p>------------------------------------</p>
     *  @param outSide 外部顏色 
     *  @see tool {@link talkdraw.DetailPane#getChildrenNode()}</p>*/
    public void setOutSideColor( Color outSide ){
        this.outSide = outSide;

        mainGC.setStroke(outSide);
        prevGC.setStroke(outSide);
        
    };
    //--------------------------------------------------------------------
    /** <p>工具顏色內外交換</p> 
     *  <p>假如要增加設定細節的元件，記得一定要去 {@link talkdraw.DetailPane#getChildrenNode()} 那裡增加回傳值</p>
     *  <p>------------------------------------</p>
     *  @see tool {@link talkdraw.DetailPane#getChildrenNode()}</p>*/
     public void swapColor(){
        //Color swap = new Color(inSide.getRed(), inSide.getGreen(), inSide.getBlue(), inSide.getOpacity());
        //setInSideColor(new Color(outSide.getRed(), outSide.getGreen(), outSide.getBlue(), outSide.getOpacity()));
        //setOutSideColor(swap);
        Color swap = inSide;
        setInSideColor(outSide);
        setOutSideColor(swap);
    };
    //--------------------------------------------------------------------
    /** 設定粗細，如果是圖形應該會變成外框粗細 
     *  <p>假如要增加設定細節的元件，記得一定要去 {@link talkdraw.DetailPane#getChildrenNode()} 那裡增加回傳值
     *  @param penSize 愈設定的粗細度 
     *  @see tool {@link talkdraw.DetailPane#getChildrenNode()}*/
    public void setLineWidth( int penSize ){ 
        this.penSize = penSize;
        mainGC.setLineWidth( penSize ); 
        prevGC.setLineWidth( penSize ); 
    };
    //--------------------------------------------------------------------
    /** 設定線與線之間的接角 
     *  <p>假如要增加設定細節的元件，記得一定要去 {@link talkdraw.DetailPane#getChildrenNode()} 那裡增加回傳值
     *  @param lineJoin 欲設定的型態
     *  @see tool {@link talkdraw.DetailPane#getChildrenNode()}*/
    public void setLineJoin(StrokeLineJoin lineJoin){
        mainGC.setLineJoin( lineJoin );
        prevGC.setLineJoin( lineJoin );
    }
    //--------------------------------------------------------------------
    /** 設定線的邊框 
     *  <p>假如要增加設定細節的元件，記得一定要去 {@link talkdraw.DetailPane#getChildrenNode()} 那裡增加回傳值
     *  @param lineCap 欲設定的型態
     *  @see tool {@link talkdraw.DetailPane#getChildrenNode()}*/
    public void setLinecap( StrokeLineCap lineCap ){
        mainGC.setLineCap( lineCap );
        prevGC.setLineCap( lineCap );
    }
    //--------------------------------------------------------------------
    /** 設定線的虛線  
     *  <p>假如要增加設定細節的元件，記得一定要去 {@link talkdraw.DetailPane#getChildrenNode()} 那裡增加回傳值
     *  @param value 欲開啟或關閉虛線效果
     *  @see tool {@link talkdraw.DetailPane#getChildrenNode()}*/
    public void setLineDashes(boolean value){
        if(value){
            mainGC.setLineDashes(penSize * 2);
            prevGC.setLineDashes(penSize * 2);
        }else{
            mainGC.setLineDashes(null);
            prevGC.setLineDashes(null);
        }
    }
    /** 設定線的虛線  
     *  <p>假如要增加設定細節的元件，記得一定要去 {@link talkdraw.DetailPane#getChildrenNode()} 那裡增加回傳值
     *  @param value 欲開啟或關閉虛線效果
     *  @param size 虛線的空白寬度
     *  @see tool {@link talkdraw.DetailPane#getChildrenNode()}*/
    public void setLineDashes(boolean value,double size){
        if(value){
            mainGC.setLineDashes(size);
            prevGC.setLineDashes(size);
        }else{
            mainGC.setLineDashes(null);
            prevGC.setLineDashes(null);
        }
    }
    //--------------------------------------------------------------------
    /** 取得 Detail Pane @return {@code [Pane]} */
    public Pane getDetailPane(){  return detailPane; }

    /** 取得ID @return {@code [Int]}*/
    public int getID(){ return ID; }

    public String getName(){ return getText(); }
    /** 清除預覽畫面 */
    public void clearPrevGC(){
        prevGC.clearRect(0, 0, prevGC.getCanvas().getWidth(), prevGC.getCanvas().getHeight());
    }
    /** 語音繪圖前的輸入提示
     * @param msg 首句提示
     * @param target 需輸入的項目
     * @param data 已輸入的資料
     * @return 提示字串
     */
    public String hintMsg(String msg, String[] target, int[] data){
        msg+="目前已輸入：";
        for (int i = 0; i < data.length; i++) {
            msg += "  " + target[i] + " = " + data[i];
        }
        return msg;
    }
    /** 語音繪圖後的確認訊息
     * @param target 需輸入的項目
     * @param data 已輸入的資料
     * @param flag 輸入是否正確
     * @return 詢問是否正確的字串
     */
    public String checkMsg(String[] target, int[] data, boolean flag){
        String msg = "\t目前屬性：";
        if (!flag)
            msg = "!! 請確認是否要畫出" + msg;
        for (int i = 0; i < target.length; i++) {
            msg += "  " + target[i] + " = " + data[i];
        }
        return msg + "\t這樣對嗎? ( 確定 )";
        //return msg+"\n這樣對嗎? ( a.確定 | b.取消 | c.重畫 )";
    }

     /** 移動方向 */
    enum Direction{
        //STOP(5,0,0),LT(7,-1,-1),T(8,0,-1),RT(9,1,-1),R(6,1,0),RU(3,1,1),U(2,0,1),LU(1,-1,1),L(4,-1,0);
        STOP("停下",0,0),LT("左上",-1,-1),T("往上",0,-1),RT("右上",1,-1),R("往右",1,0),RU("右下",1,1),U("往下",0,1),LU("左下",-1,1),L("往左",-1,0);

        private String id;
        private int x, y;
        private static HashMap<String,Direction> map = new HashMap<>();
        private Direction(String id, int x, int y){
            this.id = id; this.x = x; this.y = y;
        }

        static {
            for (Direction direction : Direction.values()) {
                map.put(direction.id, direction);
            }
        }

        public int getX(){ return x; }
        public int getY(){ return y; }
        public static Direction get(String id) {
            return map.get(id);
        }
        public String getID(){
            return this.id;
        }

    }
}