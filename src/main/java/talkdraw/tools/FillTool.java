package talkdraw.tools;

import java.util.LinkedList;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import talkdraw.componet.TextSliderBar;
import talkdraw.event.ToolEvent;
import talkdraw.imgobj.LayerClone;


/** <p>繼承了 {@link talkdraw.tools.Tool } </p> 
 *  <p>最基礎的擦子工具展示</p> */
public class FillTool extends Tool{
    /** 設定容錯率的物件 */
    private TextSliderBar errorTS;
    /** 建構子
     *  @param name 工具名稱 
     *  @param mainGC 主要畫布畫筆
     *  @param prevGC 預覽畫布畫筆*/
    public FillTool(String name, GraphicsContext mainGC, GraphicsContext prevGC){
        super(name, mainGC, prevGC);
        initialPane();
    }
    //============================================================================================
    //================================     滑鼠功能區     =========================================
    /** 當滑鼠按下時的動作 */
    @Override
    public void onMouseClick(MouseEvent e) {
        double nowX=e.getX(),nowY=e.getY();

        LayerClone clone=new LayerClone(activeLayer);
        fillAll((int)nowX,(int)nowY,(Color)outSide,(int)errorTS.getValue(),mainGC);
        //Color temp=mainGC.getCanvas().snapshot(null, null).getPixelReader().getColor((int)nowX, (int)nowY);
        //System.out.println(temp.getOpacity()+","+temp.getRed()+","+temp.getGreen()+","+temp.getBlue()+",");
        
        fireEvent(new ToolEvent(ToolEvent.FILL, "手動繪製", activeLayer, clone));
    }

    @Override
    public void onMouseRelease(MouseEvent e) {
        
        //fireEvent(new ToolEvent(ToolEvent.FILL, "手動繪製", this, this));

    }

    @Override
    public void onMouseDragging(MouseEvent e) {

    }
    @Override
    public void onMouseMove( MouseEvent e ){
        double nowX=e.getX(),nowY=e.getY();

        fillAll((int)nowX,(int)nowY,(Color)outSide,(int)errorTS.getValue(),prevGC);
    }
    //============================================================================================
    //=================================     語音設定     ==========================================
    @Override
    public String setValue(String name, String value) throws Exception {
        if(name==null)return "請輸入屬性名稱"; 
        try{
            if(value!=null) Integer.parseInt(value);
        }catch(Exception e){
            return "請輸入數字";
        }
        switch(name){
            case "誤差值":
                if(value==null)return "請輸入數字";
                errorTS.setValue(Integer.parseInt(value));
            break;
            default: throw new Exception("無此項目");
        }
        updataTool();
        return "true";
    }
    @Override
    public String speechDraw(String ... poss) throws Exception{
        int[] pos = new int[poss.length];
        for(int i=0;i<poss.length;i++){
            try{pos[i]=Integer.parseInt(poss[i]);}catch(Exception e){}
        }
        //需要的參數
        String target[]={"x","y"};
        if( pos.length < target.length ) {
            String msg="請輸入座標 x y";
            return hintMsg(msg, target, pos);
        }
        
        //參數足夠時畫出預覽畫面
         Platform.runLater(() -> {
            boolean b = false;
            while (!b) {
                b = false;
                try {
                    Thread.sleep( 5 );
                    fillAll(pos[0], pos[1], (Color)outSide, (int) errorTS.getValue(), prevGC);
                    b = true;
                } catch (Exception e) {
                }
            }

        });
       /* 
        if (pos.length == target.length) {
            // 剛好達到足夠的參數則印出確認訊息
            return checkMsg(target, pos, true);
        }else if (pos.length > target.length && checkDraw){
            checkDraw = false; */
            // 確定畫出圖形
            Platform.runLater(() -> {
                boolean b=false;
                while(!b){
                    b=false;
                    try{
                        Thread.sleep( 5 );
                        LayerClone clone = new LayerClone(activeLayer);
                        b=true;
                        drawEnter();
                        clearPrevGC();
                        //fillAll(pos[0], pos[1], outSide, (int) errorTS.getValue(), mainGC);
        
                        fireEvent(new ToolEvent(ToolEvent.FILL, "語音繪製", activeLayer, clone));
                    }catch(Exception e){ }
                }
                
            });
        /* } else {
            // 亂回答的回覆訊息
            return checkMsg(target, pos, false);
        } */

        return "true";
    }
    //============================================================================================
    //====================================     雜項     ==========================================

    @Override
    public void updataTool() {
    }
    protected void initialPane(){
        detailPane = new Pane();
        errorTS = new TextSliderBar(0, 100, 10, "誤差值");
        
        
        // GridPane 排版
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_LEFT);
        gridPane.setHgap(10.0);
        // 控制項
        gridPane.add(errorTS,0,0);
        
        // 排版用Label
        Label heightLabel = new Label();
        heightLabel.setMinHeight(60);
        heightLabel.setMaxHeight(60);
        heightLabel.setPrefHeight(60);
        gridPane.add(heightLabel, 1, 0);


        detailPane.getChildren().add(gridPane);

        initialListener();
    }
    
    protected void initialListener() {
        // 匿名函式宣告： 監聽 detailPane 的所有元件，以至於可以更動後更改 Tool
        /*detailPane.getChildren().forEach(node -> {
            node.addEventHandler(Event.ANY, e -> {
                updataTool();
            });
        });*/
    }

    /** 填滿指定顏色
     * 
     * @param startX    起始x座標
     * @param startY    起始y座標
     * @param fillColor 填滿顏色
     * @param tolerance 容許誤差
     * @param GC        畫筆
     */
    private void fillAll(int startX,int startY,Color fillColor ,int tolerance,GraphicsContext GC){

        //如果為同顏色，則不動作
        if(GC.getCanvas().snapshot(null, null).getPixelReader().getColor(startX, startY).equals(fillColor))return;
        //int count=0;
        int w=(int)mainGC.getCanvas().getWidth(),h=(int)mainGC.getCanvas().getHeight();
        //原本的顏色
        PixelReader pr=mainGC.getCanvas().snapshot(null,null).getPixelReader();
        Color originalColor=pr.getColor(startX, startY);
        double oA=originalColor.getOpacity(),oR=originalColor.getRed(),oG=originalColor.getGreen(),oB=originalColor.getBlue();
        //比較用的顏色
        Color tempColor=null;
        //遍歷元素
        LinkedList<RGB_Point> points = new LinkedList<>();
        boolean flag[][] = new boolean [(int)mainGC.getCanvas().getWidth()][(int)mainGC.getCanvas().getHeight()];
        //------------------------------------------------------------
        //這裡使用 BFS 來當作去背演算法
        points.add( new RGB_Point(startX, startY) );
        flag[startX][startY] = true;
        tolerance*=100;
        //設定起始點的顏色
        //GC.getPixelWriter().setColor(startX, startY, fillColor);

        while( points.size() > 0 ){
        
            //count++;
            RGB_Point point = points.removeFirst();
            GC.getPixelWriter().setColor(point.x, point.y, fillColor);
            //System.out.println( "0" );

            for(Direction dir = Direction.N; dir != null;  dir = dir.next() ){      //方向上加
                int x = point.x + dir.x, y = point.y + dir.y;

                //if(mainGC.isPointInPath(x, y)) continue;   //判斷是否超出邊界
                if( x < 0 || y < 0 || x >= w || y >= h ) continue;     //判斷是否超出邊界
                //System.out.println( "1" );
                

                if( !(flag[x][y]) ){
                    flag[x][y] = true;
                    //System.out.println( "2" );
                    Color temp=pr.getColor(x, y);
                    
                    if(!(tempColor!=null && temp.equals(tempColor))){
                        boolean check=false;
                        //超過容許誤差
                        if(Math.abs(oA-temp.getOpacity())*10000>tolerance) check=true;
                        if(Math.abs(oR-temp.getRed())*10000>tolerance) check=true;
                        if(Math.abs(oG-temp.getGreen())*10000>tolerance) check=true;
                        if(Math.abs(oB-temp.getBlue())*10000>tolerance) check=true;

                        GC.getPixelWriter().setColor(x, y, fillColor);
                        if(check)continue;
    
                        tempColor=temp;
                    }

                    //System.out.println( "3" );
                    //RGB_Point node = new RGB_Point(x, y);   //圖片的下一個像素節點
                    points.add( new RGB_Point(x, y) );     //增加下一個像素結點
                    //GC.getPixelWriter().setColor(x, y, fillColor);
                    
                    //System.out.println( "4" );
                }
                //double rgb=mainGC.getCanvas().snapshot(null, null).getPixelReader().get
                //if( x < 0 || y < 0 || x >= mainGC.getCanvas().getWidth() || y >= mainGC.getCanvas().getHeight() ) continue;     //判斷是否超出邊界
                
                /*if( x < 0 || y < 0 || x >= Width || y >= Height ) continue;     //判斷是否超出邊界

                int alpha = 0;      //透明值
                int rgb = img.getRGB(x, y); //取得 圖片的RGB
                if( !(flag[x][y]) ){
                    RGB_Point node = new RGB_Point(x, y);   //圖片的下一個像素節點
                    flag[x][y] = true;
                    if( colorInRange(rgb) ){    //假如顏色在範圍內(白色)
                        points.add( node );     //增加下一個像素結點
                        alpha = 255;    //設為透明
                    }
                }
                // #AARRGGBB 最前兩位為透明
                rgb = ( alpha << 24 ) | ( rgb & 0x00ffffff );
                img.setRGB(x, y, rgb);*/
            }
            //System.out.println( count );
        }
    }
    
    //=====================================================================================================================================
    /** 未完成函式，保留用 */
    /*public static void removeBackground(){
        //遍歷元素
        LinkedList<RGB_Point> points = new LinkedList<>();
        boolean flag[][] = new boolean [Width][Height];
        //------------------------------------------------------------
        //這裡使用 BFS 來當作去背演算法
        points.add( new RGB_Point(250, 250) );
        while( points.size() > 0 ){
            RGB_Point point = points.removeFirst();

            for(Direction dir = Direction.N; dir != null;  dir = dir.next() ){      //方向上加
                int x = point.getX() + dir.getX(), y = point.getY() + dir.getY();

                if( x < 0 || y < 0 || x >= Width || y >= Height ) continue;     //判斷是否超出邊界

                int alpha = 0;      //透明值
                int rgb = img.getRGB(x, y); //取得 圖片的RGB
                if( !(flag[x][y]) ){
                    RGB_Point node = new RGB_Point(x, y);   //圖片的下一個像素節點
                    flag[x][y] = true;
                    if( colorInRange(rgb) ){    //假如顏色在範圍內(白色)
                        points.add( node );     //增加下一個像素結點
                        alpha = 255;    //設為透明
                    }
                }
                // #AARRGGBB 最前兩位為透明
                rgb = ( alpha << 24 ) | ( rgb & 0x00ffffff );
                img.setRGB(x, y, rgb);
            }
            //System.out.println( alpha );
        }
    }
    /** 遍歷像素方向 */
    enum Direction{
        N(0, 1),E(1, 0),S(0, -1),W(-1 ,0);
        int x, y;
        private Direction(int x, int y){
            this.x = x; this.y = y;
        }
        public int getX(){ return x; }
        public int getY(){ return y; }
        public Direction next(){
            return ( this.equals(W) ) ? null : Direction.values()[this.ordinal() + 1];
        }
    }
    /**  儲存 RGB Color 座標 Class */
    class RGB_Point{
        int x, y;
        public RGB_Point(int x, int y){
            this.x = x; this.y = y;
        }
        public int getX(){ return x; }
        public int getY(){ return y; }
    }

    /** 將預覽畫布的圖畫在主畫布上 */
    private void drawEnter(){

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill( Color.TRANSPARENT );
        mainGC.drawImage(prevGC.getCanvas().snapshot(parameters, null), 0, 0);
    }

}