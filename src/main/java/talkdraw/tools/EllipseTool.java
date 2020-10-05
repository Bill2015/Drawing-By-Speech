package talkdraw.tools;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import talkdraw.componet.TextSliderBar;
import talkdraw.event.ToolEvent;
import talkdraw.imgobj.LayerClone;

/** <p>繼承了 {@link talkdraw.tools.Tool } </p> 
 *  <p>橢圓形工具</p> */
public class EllipseTool extends Tool{
    /** 設定尺寸的物件 */
    private TextSliderBar sizeTS;
    /**畫筆樣式圓形按鈕
     * @apiNote [0] = 圓框 [1] = 方框 */
    private RadioButton penStyleBtn[] = new RadioButton[2];
    /**設定起點圓形按鈕
     * @apiNote [0] = 角落 [1] = 圓心 */
    private RadioButton startPointBtn[] = new RadioButton[2];
    /**樣式群組 */
    private ToggleGroup penStytleGroup = new ToggleGroup();
    private ToggleGroup startPointGroup = new ToggleGroup();
    /** 是否要虛線的 勾勾選項 */
    private CheckBox dashCheckBox;
    /** 設定虛線尺寸的物件 */
    private TextSliderBar dashSizeTS;
    
    private double startX, startY;
    private double endX, endY;
    /** 建構子
     *  @param name 工具名稱 
     *  @param mainGC 主要畫布畫筆
     *  @param prevGC 預覽畫布畫筆*/
    public EllipseTool(String name, GraphicsContext mainGC, GraphicsContext prevGC){
        super(name, mainGC, prevGC);
        initialPane();
    }
    //============================================================================================
    //================================     滑鼠功能區     =========================================
    /** 當滑鼠按下時的動作 */
    @Override
    public void onMouseClick(MouseEvent e) {
        if(e.getButton()==MouseButton.SECONDARY){swapColor();}
        double nowX = e.getX(), nowY = e.getY();
        startX = endX = nowX;
        startY = endY = nowY;

        if(startPointBtn[0].isSelected()){
            prevGC.fillArc(startX < endX ? startX : endX, startY < endY ? startY : endY, Math.abs(endX - startX), Math.abs(endY - startY), 0, 360, ArcType.CHORD);
            prevGC.strokeArc(startX < endX ? startX : endX, startY < endY ? startY : endY, Math.abs(endX - startX), Math.abs(endY - startY), 0, 360, ArcType.CHORD);    
        }else{
            //寬 高
            double w=Math.abs(endX - startX),h=Math.abs(endY - startY);
            prevGC.fillArc(startX - w, startY - h, w * 2, h * 2, 0, 360, ArcType.CHORD);
            prevGC.strokeArc(startX - w, startY - h, w * 2, h * 2, 0, 360, ArcType.CHORD);
        }
        if(e.getButton()==MouseButton.SECONDARY){swapColor();}
    }

    @Override
    public void onMouseRelease(MouseEvent e) {        
        
        LayerClone clone=new LayerClone(activeLayer);
        
        if(e.getButton()==MouseButton.SECONDARY){swapColor();}
        if(startPointBtn[0].isSelected()){
            mainGC.fillArc(startX < endX ? startX : endX, startY < endY ? startY : endY, Math.abs(endX - startX), Math.abs(endY - startY), 0, 360, ArcType.CHORD);
            mainGC.strokeArc(startX < endX ? startX : endX, startY < endY ? startY : endY, Math.abs(endX - startX), Math.abs(endY - startY), 0, 360, ArcType.CHORD);    
        }else{
            //寬 高
            double w=Math.abs(endX - startX),h=Math.abs(endY - startY);
            mainGC.fillArc(startX - w, startY - h, w * 2, h * 2, 0, 360, ArcType.CHORD);
            mainGC.strokeArc(startX - w, startY - h, w * 2, h * 2, 0, 360, ArcType.CHORD);
        }
        if(e.getButton()==MouseButton.SECONDARY){swapColor();}

        
        fireEvent(new ToolEvent(ToolEvent.ELLIPASE, "手動繪製", activeLayer, clone));
    }

    @Override
    public void onMouseDragging(MouseEvent e) {
        if(e.getButton()==MouseButton.SECONDARY){swapColor();}
        double nowX = e.getX(), nowY = e.getY();
        endX = nowX;
        endY = nowY;
        if(startPointBtn[0].isSelected()){
            prevGC.fillArc(startX < endX ? startX : endX, startY < endY ? startY : endY, Math.abs(endX - startX), Math.abs(endY - startY), 0, 360, ArcType.CHORD);
            prevGC.strokeArc(startX < endX ? startX : endX, startY < endY ? startY : endY, Math.abs(endX - startX), Math.abs(endY - startY), 0, 360, ArcType.CHORD);    
        }else{
            //寬 高
            double w=Math.abs(endX - startX),h=Math.abs(endY - startY);
            prevGC.fillArc(startX - w, startY - h, w * 2, h * 2, 0, 360, ArcType.CHORD);
            prevGC.strokeArc(startX - w, startY - h, w * 2, h * 2, 0, 360, ArcType.CHORD);
        }
        if(e.getButton()==MouseButton.SECONDARY){swapColor();}
    }
    @Override
    public void onMouseMove( MouseEvent e ){

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
            case "外框寬度":
                if(value==null)return "請輸入數字";
                sizeTS.setValue(Integer.parseInt(value));
            break;
            case "圓框":
                penStyleBtn[0].setSelected(true);
            break;
            case "方框":
                penStyleBtn[1].setSelected(true);
            break;
            case "角落":
                startPointBtn[0].setSelected(true);
            break;
            case "圓心":
                startPointBtn[1].setSelected(true);
            break;
            case "虛線":
                dashCheckBox.setSelected(!dashCheckBox.isSelected());
            break;
            case "間隔":
                if(value==null)return "請輸入數字";
                if(dashCheckBox.isSelected()) dashSizeTS.setValue(Integer.parseInt(value));
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
        //判斷繪圖方式
        if(startPointBtn[0].isSelected()){

            //需要的參數
            String target[]={"x1","y1","x2","y2"};
            if( pos.length < target.length ) {
                String msg="請輸入座標 x1 y1 x2 y2";
                return hintMsg(msg, target, pos);
            }


            //參數足夠時畫出預覽畫面
            /* prevGC.fillArc(pos[0] < pos[2] ? pos[0] : pos[2], pos[1] < pos[3] ? pos[1] : pos[3], Math.abs(pos[2] - pos[0]), Math.abs(pos[3] - pos[1]), 0, 360, ArcType.CHORD);
            prevGC.strokeArc(pos[0] < pos[2] ? pos[0] : pos[2], pos[1] < pos[3] ? pos[1] : pos[3], Math.abs(pos[2] - pos[0]), Math.abs(pos[3] - pos[1]), 0, 360, ArcType.CHORD);    
           

            if(pos.length == target.length){
                 //剛好達到足夠的參數則印出確認訊息
                return checkMsg(target, pos, true);
            }else if (pos.length > target.length && checkDraw){
                checkDraw = false; */
                //確定畫出圖形
                Platform.runLater(() -> {
                    boolean b=false;
                    while(!b){
                        b=false;
                        try{
                            Thread.sleep( 5 );
                            LayerClone clone = new LayerClone(activeLayer);
                            b=true;
                            clearPrevGC();
                            mainGC.fillArc(pos[0] < pos[2] ? pos[0] : pos[2], pos[1] < pos[3] ? pos[1] : pos[3], Math.abs(pos[2] - pos[0]), Math.abs(pos[3] - pos[1]), 0, 360, ArcType.CHORD);
                            mainGC.strokeArc(pos[0] < pos[2] ? pos[0] : pos[2], pos[1] < pos[3] ? pos[1] : pos[3], Math.abs(pos[2] - pos[0]), Math.abs(pos[3] - pos[1]), 0, 360, ArcType.CHORD);    
                            
                            fireEvent(new ToolEvent(ToolEvent.ELLIPASE, "語音繪製", activeLayer, clone));
                        }catch(Exception e){ }
                    }
                    
                });
            /* }else{
                //亂回答的回覆訊息
                return checkMsg(target, pos, false);
            } */
        /**************************************************************************************/
        }else{

            //需要的參數
            String target[]={"x","y","w","h"};
            if( pos.length < target.length ) {
                String msg="請輸入圓心座標 x y 及寬高 w h";
                return hintMsg(msg, target, pos);
            }

            
            //參數足夠時畫出預覽畫面
            //寬 高
            //double w=Math.abs(pos[2] - pos[0]),h=Math.abs(pos[3] - pos[1]);
            double w=pos[2],h=pos[3];
           /*  prevGC.fillArc(pos[0] - w, pos[1] - h, w * 2, h * 2, 0, 360, ArcType.CHORD);
            prevGC.strokeArc(pos[0] - w, pos[1] - h, w * 2, h * 2, 0, 360, ArcType.CHORD);


            if(pos.length == target.length){
                 //剛好達到足夠的參數則印出確認訊息
                return checkMsg(target, pos, true);
            }else if (pos.length > target.length && checkDraw){
                checkDraw = false; */
                //確定畫出圖形
                Platform.runLater(() -> {
                    boolean b=false;
                    while(!b){
                        b=false;
                        try{
                            Thread.sleep( 5 );
                            LayerClone clone = new LayerClone(activeLayer);
                            b=true;
                            clearPrevGC();
                            mainGC.fillArc(pos[0] - w, pos[1] - h, w * 2, h * 2, 0, 360, ArcType.CHORD);
                            mainGC.strokeArc(pos[0] - w, pos[1] - h, w * 2, h * 2, 0, 360, ArcType.CHORD);
        
                            fireEvent(new ToolEvent(ToolEvent.ELLIPASE, "語音繪製", activeLayer, clone));
                        }catch(Exception e){ }
                    }
                    
                });
            /* }else{
                //亂回答的回覆訊息
                return checkMsg(target, pos, false);
            } */
        }
        return "true";

    }
    //============================================================================================
    //====================================     雜項     ==========================================

    @Override
    public void updataTool() {
        setLineWidth( (int)sizeTS.getValue() );
        setLinecap(getPenStyle());
        setColor(inSide, outSide);
        dashSizeTS.setDisable(!dashCheckBox.isSelected());
        setLineDashes(dashCheckBox.isSelected(),dashSizeTS.getValue()+penSize);
    }
    protected void initialPane(){
        detailPane = new Pane();
        sizeTS = new TextSliderBar(1, 500, 50, "外框寬度");
        dashCheckBox = new CheckBox("虛線");
        dashSizeTS = new TextSliderBar(1, 500, 100, "間隔");
        radioBtnInitial();
        
        
        // GridPane 排版
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_LEFT);
        gridPane.setHgap(10.0);
        // 控制項
        gridPane.add(sizeTS,0,0);
        gridPane.add(startPointBtn[0], 1, 0);
        gridPane.add(startPointBtn[1], 2, 0);
        gridPane.add(dashCheckBox, 3, 0);
        gridPane.add(dashSizeTS, 4, 0);
        gridPane.add(penStyleBtn[0], 5, 0);
        gridPane.add(penStyleBtn[1], 6, 0);
        
        // 排版用Label
        Label heightLabel = new Label();
        heightLabel.setMinHeight(60);
        heightLabel.setMaxHeight(60);
        heightLabel.setPrefHeight(60);
        gridPane.add(heightLabel, 7, 0);

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
        dashSizeTS.getValueProperty().addListener((obser, oldVal, newVal) -> {
            setLineDashes(dashCheckBox.isSelected(),dashSizeTS.getValue()+penSize);
        });
        dashCheckBox.selectedProperty().addListener((obser, oldval, newVal) -> {
            dashSizeTS.setDisable(!newVal);
            penStyleBtn[0].setDisable(!newVal);
            penStyleBtn[1].setDisable(!newVal);
            setLineDashes(dashCheckBox.isSelected(),dashSizeTS.getValue()+penSize);
        });
        // 匿名函式宣告： 監聽 detailPane 的所有元件，以至於可以更動後更改 Tool
        /*detailPane.getChildren().forEach(node -> {
            node.addEventHandler(Event.ANY, e -> {
                updataTool();
            });
        });*/
    }
    /** 初始化圓形按鍵 */
    private void radioBtnInitial() {
        // 初始化按鍵
        for (int i = 0; i < 2; i++) {
            penStyleBtn[i] = new RadioButton();
            penStyleBtn[i].setToggleGroup(penStytleGroup);
            penStyleBtn[i].setPrefSize(60, 15);
            //===============================
            startPointBtn[i] = new RadioButton();
            startPointBtn[i].setToggleGroup(startPointGroup);
            startPointBtn[i].setPrefSize(60, 15);
        }
        penStyleBtn[0].setText("圓框");
        penStyleBtn[1].setText("方框");
        penStyleBtn[1].setSelected(true);//預設方框
        //===============================
        startPointBtn[0].setText("角落");
        startPointBtn[1].setText("圓心");
        startPointBtn[0].setSelected(true);//預設角落
    }
    /** 取得 Pen Style 的值 (畫筆樣式 {@code 圓框 或 方框} 的值) */
    private StrokeLineCap getPenStyle(){ 
        if(penStyleBtn[0].isSelected())return StrokeLineCap.ROUND;
        if(penStyleBtn[1].isSelected())return StrokeLineCap.SQUARE;
        return null;
    }

}