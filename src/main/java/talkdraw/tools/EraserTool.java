package talkdraw.tools;


import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import talkdraw.componet.TextSliderBar;
import talkdraw.event.ToolEvent;
import talkdraw.imgobj.LayerClone;

/** <p>繼承了 {@link talkdraw.tools.Tool } </p> 
 *  <p>最基礎的擦子工具展示</p> */
public class EraserTool extends Tool{
    /** 設定尺寸的物件 */
    private TextSliderBar sizeTS;
    /** 設定移動量的物件 */
    private TextSliderBar moveTS;
    
    //private double x, y;
    private double startX, startY;
    private double endX, endY;
    private Color nullColor = Color.TRANSPARENT;
    /** 暫存圖層 */
    private LayerClone clone;
    /** 是否為語音繪圖移動階段 */
    private boolean speechMove = false;
    /** 是否超出邊界 */
    private boolean outBound = false;
    /** 語音移動次數 */
    private int moveCount = 0;
    /** 剩餘移動次數 */
    private int moveTimes = 0;
    /** 語音移動方向 */
    private Direction moveDirection=Direction.STOP;
    /** 建構子
     *  @param name 工具名稱 
     *  @param mainGC 主要畫布畫筆
     *  @param prevGC 預覽畫布畫筆*/
    public EraserTool(String name, GraphicsContext mainGC, GraphicsContext prevGC){
        super(name, mainGC, prevGC);
        initialPane();
    }
    //============================================================================================
    //================================     滑鼠功能區     =========================================
    /** 當滑鼠按下時的動作 */
    @Override
    public void onMouseClick(MouseEvent e) {
        clone=new LayerClone(activeLayer);
        double nowX = e.getX(), nowY = e.getY();
        startX = nowX; startY=nowY;
        //起始點的方形
        /*for (double x = (nowX - (penSize >> 1)) >= 0 ? nowX - (penSize >> 1) : 0; x < nowX + (double)penSize / 2; x++) {
            for (double y = (nowY - (penSize >> 1)) >= 0 ? nowY - (penSize >> 1) : 0; y < nowY + (double)penSize / 2; y++) {
                mainGC.getPixelWriter().setColor((int)x, (int)y, nullColor);
            }
        }*/
        mainGC.clearRect((nowX - (penSize >> 1)) , (nowY - (penSize >> 1)), penSize, penSize);

        
    }

    @Override
    public void onMouseRelease(MouseEvent e) {

        fireEvent(new ToolEvent(ToolEvent.ERASER, "手動繪製", activeLayer, clone));
    }

    @Override
    public void onMouseDragging(MouseEvent e) {
        double nowX = e.getX(), nowY = e.getY();
        endX=nowX; endY=nowY;
        //將起始的方形外框與結束的方形外框連線
        int ps = penSize >> 1;
        for (int i = -ps; i <= ps; i++) {
            BresenhamLine((int) startX + i, (int) startY - ps, (int) endX + i, (int) endY - ps);
            BresenhamLine((int) startX + i, (int) startY + ps, (int) endX + i, (int) endY + ps);
            BresenhamLine((int) startX - ps, (int) startY + i, (int) endX - ps, (int) endY + i);
            BresenhamLine((int) startX + ps, (int) startY + i, (int) endX + ps, (int) endY + i);
        }

        startX=nowX; startY=nowY;
    }
    @Override
    public void onMouseMove( MouseEvent e ){
        prevGC.setLineWidth(1);//設定預覽外框寬度
        prevGC.setStroke(Color.BLACK);//設定外框顏色
        double nowX = e.getX(), nowY = e.getY();

        prevGC.strokeRect(nowX - (penSize >> 1), nowY - (penSize >> 1), penSize, penSize);
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
            case "尺寸":
                if(value==null)return "請輸入數字";
                sizeTS.setValue(Integer.parseInt(value));
            break;
            case "移動量":
                if(value==null)return "請輸入數字";
                moveTS.setValue(Integer.parseInt(value));
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
        String target[]={"x","y"};
        if( pos.length < target.length ) {
            speechMove=false;
            String msg="請輸入起點座標 x y";
            return hintMsg(msg, target, pos);
        }
        
        //避免重複畫起始點
        if(speechMove==false){
            speechMove=true;moveCount=0;
            endX=startX=pos[0]; endY=startY=pos[1];
            Platform.runLater(() -> {
                boolean b = false;
                while (!b) {
                    b = false;
                    try {
                        Thread.sleep(5);
                        clone = new LayerClone(activeLayer);
                        b = true;
                    } catch (Exception e) {
                    }
                }
            });
            try{Thread.sleep(10);}catch(Exception e){}
            // 參數足夠時畫出預覽畫面
            mainGC.clearRect((startX - (penSize >> 1)) , (startY - (penSize >> 1)), penSize, penSize);
                
            clearPrevGC();
            prevGC.setLineWidth(1);//設定預覽外框寬度
            prevGC.setStroke(Color.BLACK);//設定外框顏色

            prevGC.strokeRect(startX - (penSize >> 1), startY - (penSize >> 1), penSize, penSize);
        }
        
        if (pos.length > target.length && checkDraw){
            checkDraw = false;
            // 確定畫出圖形
            Platform.runLater(() -> {
                boolean b=false;
                while(!b){
                    b=false;
                    try{
                        Thread.sleep( 5 );
                        clearPrevGC();
                        moveDirection=Direction.STOP;
                        moveTimes=0;
        
                        fireEvent(new ToolEvent(ToolEvent.ERASER, "語音繪製", activeLayer, clone));
                        
                        b=true;
                    }catch(Exception e){ }
                }
            });
        }else if (pos.length >= target.length) {
            // 有起始點之後
            for (int j = target.length + moveCount; j < pos.length; j++) {
                if(Direction.get(poss[j])!=null){
                    moveDirection = Direction.get(poss[j]);
                    moveTimes=0;
                } 
                else {
                    if(!moveDirection.equals(Direction.STOP)){
                        moveTimes=pos[j];
                        outBound=false;
                    } 
                } 
                

                moveCount++;
            }

            return "移動[方向+次數]? (方向: 停下 | 左下 | 往下 | 右下 | 往左 | 往右 | 左上 | 往上 | 右上 )  目前狀態: 方向>"+moveDirection.getID()+" 次數>"+moveTimes+"  結束? ( 確定 )";
        } 

        speechMove=false;
        return "true";

    }
    //============================================================================================
    //====================================     雜項     ==========================================

    @Override
    public void updataTool() {
        setLineWidth( sizeTS.getValue() );
    }
    
    protected void initialPane(){
        detailPane = new Pane();
        sizeTS = new TextSliderBar(1, 500, 50, "尺寸");
        moveTS = new TextSliderBar(1, 500, 20, "移動量");
        
        // GridPane 排版
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_LEFT);
        gridPane.setHgap(10.0);
        // 控制項
        gridPane.add(sizeTS,0,0);
        gridPane.add(moveTS,1,0);
        
        // 排版用Label
        Label heightLabel = new Label();
        heightLabel.setMinHeight(60);
        heightLabel.setMaxHeight(60);
        heightLabel.setPrefHeight(60);
        gridPane.add(heightLabel, 2, 0);


        detailPane.getChildren().add(gridPane);

        initialListener();
    }
    
    protected void initialListener() {
        sizeTS.getValueProperty().addListener( (obser, oldVal, newVal) -> {
            setLineWidth( newVal.intValue() );
        } );
        
    }
    
    /** 布雷森漢姆直線演算法<p>
    * 將(x0,y0)到(x1,y1)的線上設為透明
    */
    private void BresenhamLine(int x0, int y0, int x1, int y1) {
        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
        int temp;
        if (steep) {
            temp = x0;
            x0 = y0;
            y0 = temp;

            temp = x1;
            x1 = y1;
            y1 = temp;
        }
        if (x0 > x1) {
            temp = x0;
            x0 = x1;
            x1 = temp;

            temp = y0;
            y0 = y1;
            y1 = temp;
        }
        int deltax = x1 - x0;
        int deltay = Math.abs(y1 - y0);
        int error = deltax / 2;
        int ystep;
        int y = y0;
        ystep = y0 < y1 ? 1 : -1;
        for (int x = x0; x <= x1; x++) {
            if (steep) {
                mainGC.getPixelWriter().setColor((int) y, (int) x, nullColor);
            } else {
                mainGC.getPixelWriter().setColor((int) x, (int) y, nullColor);
            }
            error = error - deltay;
            if (error < 0) {
                y = y + ystep;
                error = error + deltax;
            }

        }
    }

    
    /** 語音操控繪圖時的移動 */
    public void move(){
        
        Platform.runLater(() -> {
            boolean b = false;
            while (!b) {
                b = false;
                try {
                    Thread.sleep(5);

                    if (moveDirection == Direction.STOP)
                        return;

                    endX = startX + moveDirection.getX() * moveTS.getValue();
                    endY = startY + moveDirection.getY() * moveTS.getValue();

                    // 將起始的方形外框與結束的方形外框連線
                    int ps = penSize >> 1;
                    for (int i = -ps; i <= ps; i++) {
                        BresenhamLine((int) startX + i, (int) startY - ps, (int) endX + i, (int) endY - ps);
                        BresenhamLine((int) startX + i, (int) startY + ps, (int) endX + i, (int) endY + ps);
                        BresenhamLine((int) startX - ps, (int) startY + i, (int) endX - ps, (int) endY + i);
                        BresenhamLine((int) startX + ps, (int) startY + i, (int) endX + ps, (int) endY + i);
                    }
                    

                    startX = endX;
                    startY = endY;
                        
                    clearPrevGC();
                    prevGC.setLineWidth(1);//設定預覽外框寬度
                    prevGC.setStroke(Color.BLACK);//設定外框顏色

                    prevGC.strokeRect(startX - (penSize >> 1), startY - (penSize >> 1), penSize, penSize);
                    moveTimes--;
                    /** 邊界判斷 */
                    ps=penSize>>1+penSize%2;
                    if(endX<0-ps || endY<0-ps || endX>mainGC.getCanvas().getWidth()+ps || endY>mainGC.getCanvas().getHeight()){
                        outBound=true;
                        moveTimes=0;
                    }
                    System.out.println("move");
                    b = true;
                } catch (Exception e) {
                }
            }
        });
    }
    public boolean isMove(){
        if(speechMove && moveTimes>0) return speechMove;
        return false;
    }

    public boolean isOutBound(){
        boolean temp = outBound;
        outBound = false;
        return temp;
    }

}