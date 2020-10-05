package talkdraw.tools;


import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import talkdraw.componet.TextSliderBar;
import talkdraw.event.ToolEvent;
import talkdraw.imgobj.LayerClone;

/** <p>繼承了 {@link talkdraw.tools.Tool } </p> 
 *  <p>最基礎的繪筆工具展示</p> */
public class PencilTool extends Tool{
    /** 設定尺寸的物件 */
    private TextSliderBar sizeTS;
    /** 設定移動量的物件 */
    private TextSliderBar moveTS;
    /**畫筆樣式圓形按鈕
     * @apiNote [0] = 圓框 [1] = 方框 */
    private RadioButton penStyleBtn[] = new RadioButton[2];
    /**樣式群組 */
    private ToggleGroup penStytleGroup = new ToggleGroup();
    private double startX, startY;
    private double endX, endY;
    /** 暫存圖層 */
    private LayerClone clone;
    /** 是否為語音繪圖移動階段 */
    private boolean speechMove = false;
    /** 是否超出邊界 */
    private boolean outBound = false;
    /** 語音已移動次數 */
    private int moveCount = 0;
    /** 剩餘移動次數 */
    private int moveTimes = 0;
    /** 語音移動方向 */
    private Direction moveDirection=Direction.STOP;
    /** 可以用來將圖片做成繪筆 */
    private Image img;//=new Image("icons/NoColorBackground.jpeg");
     /** 建構子
     *  @param name 工具名稱 
     *  @param mainGC 主要畫布畫筆
     *  @param prevGC 預覽畫布畫筆*/
    public PencilTool(String name, GraphicsContext mainGC, GraphicsContext prevGC){
        super(name, mainGC, prevGC);
        initialPane();
    }
    //============================================================================================
    //================================     滑鼠功能區     =========================================
    /** 當滑鼠按下時的動作 */
    @Override
    public void onMouseClick(MouseEvent e) {

        clone = new LayerClone( activeLayer );
        if (e.getButton() == MouseButton.SECONDARY) {
            swapColor();
        }
        double nowX = e.getX(), nowY = e.getY();
        //mainGC.strokeLine(nowX, nowY, x, y);
        startX = endX = nowX;
        startY = endY = nowY;
        if (mainGC.getLineCap() == StrokeLineCap.SQUARE) {
            //起始點的方形
            for (double x = (nowX - (penSize >> 1)) >= 0 ? nowX - (penSize >> 1) : 0; x < nowX + (double)penSize / 2; x++) {
                for (double y = (nowY - (penSize >> 1)) >= 0 ? nowY - (penSize >> 1) : 0; y < nowY + (double)penSize / 2; y++) {
                    mainGC.getPixelWriter().setColor((int)x, (int)y, (Color)outSide);
                }
            }
        }
        else if (mainGC.getLineCap() == StrokeLineCap.ROUND)
            mainGC.strokeLine(startX, startY, endX, endY);
        
        if (e.getButton() == MouseButton.SECONDARY) {
            swapColor();
        }
        
    }

    @Override
    public void onMouseRelease(MouseEvent e) {
        fireEvent(new ToolEvent(ToolEvent.PENCIL, "手動繪製", activeLayer, clone));
    }

    @Override
    public void onMouseDragging(MouseEvent e) {
        if(e.getButton()==MouseButton.SECONDARY){swapColor();}

        double nowX = e.getX(), nowY = e.getY();
        endX=nowX; endY=nowY;

        if(mainGC.getLineCap()==StrokeLineCap.SQUARE){
            //將起始的方形外框與結束的方形外框連線
            int ps = penSize >> 1;
            for (int i = -ps; i <= ps; i++) {
                BresenhamLine((int) startX + i, (int) startY - ps, (int) endX + i, (int) endY - ps, mainGC);
                BresenhamLine((int) startX + i, (int) startY + ps, (int) endX + i, (int) endY + ps, mainGC);
                BresenhamLine((int) startX - ps, (int) startY + i, (int) endX - ps, (int) endY + i, mainGC);
                BresenhamLine((int) startX + ps, (int) startY + i, (int) endX + ps, (int) endY + i, mainGC);
            }
        }
        else if(mainGC.getLineCap()==StrokeLineCap.ROUND)
            mainGC.strokeLine(startX, startY, endX, endY);

        startX=nowX; startY=nowY;
        
        if(e.getButton()==MouseButton.SECONDARY){swapColor();}

    }
    @Override
    public void onMouseMove( MouseEvent e ){
        prevGC.setLineWidth(1);//設定預覽外框寬度
        double nowX = e.getX(), nowY = e.getY();

        if(mainGC.getLineCap()==StrokeLineCap.SQUARE)
            prevGC.strokeRect(nowX - (penSize >> 1), nowY - (penSize >> 1), penSize, penSize);
        else if(mainGC.getLineCap()==StrokeLineCap.ROUND)
            prevGC.strokeArc(nowX - (penSize >> 1), nowY - (penSize >> 1), penSize, penSize, 0, 360, ArcType.CHORD);

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
            case "圓框":
                penStyleBtn[0].setSelected(true);
            break;
            case "方框":
                penStyleBtn[1].setSelected(true);
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
        //需要的參數
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
            if(mainGC.getLineCap()==StrokeLineCap.SQUARE){
                //起始點的方形
                for (double x = (endX - (penSize >> 1)) >= 0 ? endX - (penSize >> 1) : 0; x < endX + (double)penSize / 2; x++) {
                    for (double y = (endY - (penSize >> 1)) >= 0 ? endY - (penSize >> 1) : 0; y < endY + (double)penSize / 2; y++) {
                        mainGC.getPixelWriter().setColor((int)x, (int)y, (Color)outSide);
                    }
                }
                //外框
                clearPrevGC();
                prevGC.setStroke(outSide.invert());
                prevGC.setLineWidth(1);//設定預覽外框寬度
                prevGC.strokeRect(endX - (penSize >> 1), endY - (penSize >> 1), penSize, penSize);
                prevGC.setStroke(outSide);
            }
            else if(mainGC.getLineCap()==StrokeLineCap.ROUND){
                mainGC.setLineWidth(penSize);//設定預覽外框寬度
                mainGC.strokeLine(startX, startY, endX, endY);
                //外框
                clearPrevGC();
                prevGC.setStroke(outSide.invert());
                prevGC.setLineWidth(1);//設定預覽外框寬度
                prevGC.strokeArc(endX - (penSize >> 1), endY - (penSize >> 1), penSize, penSize, 0, 360, ArcType.CHORD);
                prevGC.setStroke(outSide);
            }
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
        
                        fireEvent(new ToolEvent(ToolEvent.PENCIL, "語音繪製", activeLayer, clone));
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
        setLinecap(getPenStyle());
        setColor(inSide, outSide);
        setLineDashes(false);
    }
    protected void initialPane(){
        detailPane = new Pane();
        sizeTS = new TextSliderBar(1, 500, 100, "尺寸");
        moveTS = new TextSliderBar(1, 500, 20, "移動量");
        
        styleBtnInitial();
        
        
        // GridPane 排版
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_LEFT);
        gridPane.setHgap(10.0);
        // 控制項
        gridPane.add(sizeTS,0,0);
        gridPane.add(penStyleBtn[0], 1, 0);
        gridPane.add(penStyleBtn[1], 2, 0);
        gridPane.add(moveTS,3,0);

        // 排版用Label
        Label heightLabel = new Label();
        heightLabel.setMinHeight(60);
        heightLabel.setMaxHeight(60);
        heightLabel.setPrefHeight(60);
        gridPane.add(heightLabel, 4, 0);

        detailPane.getChildren().add(gridPane);

        initialListener();
    }
    
    protected void initialListener() {
        sizeTS.getValueProperty().addListener((obser, oldVal, newVal) -> {
            setLineWidth(newVal.intValue());
        });
        penStytleGroup.selectedToggleProperty().addListener((obser, oldval, newVal) -> {
            setLinecap(getPenStyle());
        });

        // 匿名函式宣告： 監聽 detailPane 的所有元件，以至於可以更動後更改 Tool
        /*
         * detailPane.getChildren().forEach(node->{ node.addEventHandler( Event.ANY, e
         * -> { updataTool(); } ); });
         */
    }
    /** 初始化圓形按鍵 */
    private void styleBtnInitial() {
        // 初始化按鍵
        for (int i = 0; i < 2; i++) {
            penStyleBtn[i] = new RadioButton();
            //penStyleBtn[i].setFont(new Font("微軟正黑體", 16));
            penStyleBtn[i].setToggleGroup(penStytleGroup);
            penStyleBtn[i].setPrefSize(60, 15);
            //penStyleBtn[i].setTextAlignment(TextAlignment.RIGHT); // 設定文字對其方式
            //penStyleBtn[i].setContentDisplay(ContentDisplay.RIGHT); // 設定內容的排版
        }
        penStyleBtn[0].setText("圓框");
        penStyleBtn[1].setText("方框");
        penStyleBtn[1].setSelected(true);//預設方框
    }
    /** 取得 Pen Style 的值 (畫筆樣式 {@code 圓框 或 方框} 的值) */
    private StrokeLineCap getPenStyle(){ 
        if(penStyleBtn[0].isSelected())return StrokeLineCap.ROUND;
        if(penStyleBtn[1].isSelected())return StrokeLineCap.SQUARE;
        return null;
    }
    /** 布雷森漢姆直線演算法<p>
    * 將(x0,y0)到(x1,y1)的線上設為指定顏色
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param gc 要畫的地方
     */
    private void BresenhamLine(int x0, int y0, int x1, int y1, GraphicsContext gc) {
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
                gc.getPixelWriter().setColor((int) y, (int) x, (Color)outSide);
            } else {
                gc.getPixelWriter().setColor((int) x, (int) y, (Color)outSide);
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

                    if (mainGC.getLineCap() == StrokeLineCap.SQUARE) {
                        // 將起始的方形外框與結束的方形外框連線
                        int ps = penSize >> 1;
                        for (int i = -ps; i <= ps; i++) {
                            BresenhamLine((int) startX + i, (int) startY - ps, (int) endX + i, (int) endY - ps, mainGC);
                            BresenhamLine((int) startX + i, (int) startY + ps, (int) endX + i, (int) endY + ps, mainGC);
                            BresenhamLine((int) startX - ps, (int) startY + i, (int) endX - ps, (int) endY + i, mainGC);
                            BresenhamLine((int) startX + ps, (int) startY + i, (int) endX + ps, (int) endY + i, mainGC);
                        }
                        //外框
                        clearPrevGC();
                        prevGC.setStroke(outSide.invert());
                        prevGC.setLineWidth(1);//設定預覽外框寬度
                        prevGC.strokeRect(endX - (penSize >> 1), endY - (penSize >> 1), penSize, penSize);
                        prevGC.setStroke(outSide);
                    } else if (mainGC.getLineCap() == StrokeLineCap.ROUND) {
                        mainGC.setLineWidth(penSize);// 設定預覽外框寬度
                        mainGC.strokeLine(startX, startY, endX, endY);
                        //外框
                        clearPrevGC();
                        prevGC.setStroke(outSide.invert());
                        prevGC.setLineWidth(1);//設定預覽外框寬度
                        prevGC.strokeArc(endX - (penSize >> 1), endY - (penSize >> 1), penSize, penSize, 0, 360, ArcType.CHORD);
                        prevGC.setStroke(outSide);
          
                    }

                    startX = endX;
                    startY = endY;
                    moveTimes--;
                    /** 邊界判斷 */
                    int ps=penSize>>1+penSize%2;
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