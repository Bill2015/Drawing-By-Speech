package talkdraw;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import talkdraw.animation.ScaleTransitionBuilder;
import talkdraw.tools.*;
import javafx.scene.control.Toggle;

import java.util.Arrays;
import java.util.HashMap;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;

public class ToolBar extends VBox{
    /** 最大寬度 */
    public final int MAX_WIDTH = 100;
    /** 工具 Icon 圖片名稱 */
    private final String ICONS[] = {"select","pencil","eraser","line","rectangle","circle","ellipse","fill","text"};
    private final String ICONS_NAME[] = {"選擇","畫筆","橡皮擦","直線","矩形","圓形","橢圓形","填滿","文字"};
    /** 將主程式參考進來，以方面更動其他物件 */
    private final App APP;

    /** 內部顏色色塊 */
    private ColorPicker INSIDE_COLORPICKER;
    /** 外框顏色色塊 */
    private ColorPicker OUTSIDE_COLORPICKER;

    /** 工具群組 */
    private ToggleGroup TOOL_GROUP;
    /** 工具物件 MAP */
    private HashMap<String,Tool> TOOL_MAP;
    /** 目前選擇的工具 */
    private Tool NOW_Tool;
    /** 設定畫布大小的工具 */
    public SelectTool SELECT_TOOL;  

    /**  <p>畫筆的顏色</p><p> {@code inSide} 是指內部 </p><p> {@code outSide} 是指邊框 </p>  */
    public Color inSide, outSide;
    public ToolBar(App APP) {
        super(0);
        this.APP = APP;
    
        TOOL_GROUP = new ToggleGroup();
        TOOL_MAP = new HashMap<String,Tool>();

        //toolsInitia(APP.DRAW_AREA.getUpponPane().getPrevGC());

        SELECT_TOOL = new SelectTool(ICONS_NAME[0], APP);
        TOOL_MAP.put(ICONS_NAME[0], SELECT_TOOL);

        TOOL_MAP.put(ICONS_NAME[1],  new PencilTool(     ICONS_NAME[1], null, null));
        TOOL_MAP.put(ICONS_NAME[2],  new EraserTool(     ICONS_NAME[2], null, null));
        TOOL_MAP.put(ICONS_NAME[3],  new LineTool(       ICONS_NAME[3], null, null));
        TOOL_MAP.put(ICONS_NAME[4],  new RectangleTool(  ICONS_NAME[4], null, null));
        TOOL_MAP.put(ICONS_NAME[5],  new CircleTool(     ICONS_NAME[5], null, null));
        TOOL_MAP.put(ICONS_NAME[6],  new EllipseTool(    ICONS_NAME[6], null, null));
        TOOL_MAP.put(ICONS_NAME[7],  new FillTool(       ICONS_NAME[7], null, null));
        TOOL_MAP.put(ICONS_NAME[8],  new TextTool(       ICONS_NAME[8], null, null));

        
        /*ToggleButton[] Tools = new ToggleButton[]{
            new ToggleButton("選擇"), 
            new ToggleButton("畫筆"), 
            new ToggleButton("橡皮擦"), 
            new ToggleButton("直線"), 
            new ToggleButton("矩形"), 
            new ToggleButton("圓形"), 
            new ToggleButton("橢圓形"), 
            new ToggleButton("填滿"), 
            new ToggleButton("文字")
        };*/
        /*for(Tool tool:TOOL_MAP.values()){
            tool.setMinWidth(90);
            tool.setMinHeight(45);
            tool.setToggleGroup(TOOL_GROUP);
            tool.setCursor(Cursor.HAND);

            Image image = new Image("icons/" + ICONS[i] + ".png", 30, 30, false ,true);
            tool.setGraphic( new ImageView(image) );
            tool.setContentDisplay( ContentDisplay.LEFT );
            
            tool.setUserData(ICONS[i]); //便於監聽
            getChildren().addAll( tool );
        }*/
         for( int i = 0; i < TOOL_MAP.size(); i++ ){
            Tool tool = TOOL_MAP.get(ICONS_NAME[i]);
            tool.setMinWidth(90);
            tool.setMinHeight(45);
            tool.setToggleGroup(TOOL_GROUP);
            tool.setCursor(Cursor.HAND);

            Image image = new Image("icons/" + ICONS_NAME[i] + ".png", 30, 30, false ,true);
            tool.setGraphic( new ImageView(image) );
            tool.setContentDisplay( ContentDisplay.LEFT );
            
            tool.setUserData(ICONS_NAME[i]); //便於監聽
            getChildren().addAll( tool );
        } 

        //getChildren().addAll( Tools );
        setPadding(new Insets(5));
        setStyle("-fx-background-color: #5e5e5e");
        setPrefWidth(MAX_WIDTH);
        setMinWidth(0);

        colorpickerInitial();
    }
    //===========================================================================
    //========================    初始化(含監聽器)    ============================
    /** 初始化 COLORPICKER  */
    private void colorpickerInitial() {

        INSIDE_COLORPICKER = new ColorPicker(Color.WHITE); 
        OUTSIDE_COLORPICKER = new ColorPicker(Color.BLACK);
        inSide = Color.WHITE;
        outSide = Color.BLACK;

        // 裝 color piker 的容器
        Pane outPane = new Pane(OUTSIDE_COLORPICKER), inPane = new Pane(INSIDE_COLORPICKER), containerColor = new Pane(outPane, inPane);

        Tooltip tp1=new Tooltip("內部顏色"),tp2=new Tooltip("外框顏色");
        tp1.setShowDelay(new Duration(0));
        tp2.setShowDelay(new Duration(0));
        INSIDE_COLORPICKER.setTooltip(tp1);
        OUTSIDE_COLORPICKER.setTooltip(tp2);
        
        
        Platform.runLater(()->{
            outPane.setBackground(new Background(
                    new BackgroundImage(new Image("icons/NoColorBackground.jpeg"), null, null, null, null)));
            inPane.setBackground(new Background(
                    new BackgroundImage(new Image("icons/NoColorBackground.jpeg"), null, null, null, null)));

            //初始外觀
            outPane.setPrefSize(80,80);
            outPane.setTranslateX(0);
            outPane.setTranslateY(0);
            outPane.getStyleClass().setAll("color-pane");
            OUTSIDE_COLORPICKER.setPrefSize(80,80);
            OUTSIDE_COLORPICKER.setTranslateX(-11);
            OUTSIDE_COLORPICKER.setStyle("-fx-color-rect-width: 80; -fx-color-rect-height: 80");
            //OUTSIDE_COLORPICKER.getStyleClass().add("button");
            OUTSIDE_COLORPICKER.getStyleClass().setAll("color-picker");

            inPane.setPrefSize(40,40);
            inPane.setTranslateX(20);
            inPane.setTranslateY(20);
            inPane.getStyleClass().setAll("color-pane");
            INSIDE_COLORPICKER.setPrefSize(40,40);
            INSIDE_COLORPICKER.setTranslateX(-11);
            INSIDE_COLORPICKER.setStyle("-fx-color-rect-width: 40; -fx-color-rect-height: 40");
            //INSIDE_COLORPICKER.getStyleClass().add("button");
            INSIDE_COLORPICKER.getStyleClass().setAll("color-picker");

            containerColor.setTranslateX(5);
            containerColor.setTranslateY(10);
                
        });

        getChildren().addAll( containerColor );
        
        //設定監聽
        INSIDE_COLORPICKER.setOnAction((ActionEvent t) -> {
            Platform.runLater(() -> {
                inSide = INSIDE_COLORPICKER.getValue();
                if (!TOOL_MAP.get(ICONS_NAME[0]).equals(NOW_Tool)) { NOW_Tool.setInSideColor( inSide ); }
                NOW_Tool.updataTool();
            });
        });
        OUTSIDE_COLORPICKER.setOnAction((ActionEvent t) -> {
            Platform.runLater(() -> {
                outSide = OUTSIDE_COLORPICKER.getValue();
                if (!TOOL_MAP.get(ICONS_NAME[0]).equals(NOW_Tool)) { NOW_Tool.setOutSideColor( outSide ); }
                NOW_Tool.updataTool();
            });
        });
        APP.MAIN.getLayerList().getActiveLayerProperty().addListener((obser, oldVal, newVal)->{
            Platform.runLater(() -> {
                NOW_Tool.setMainGC(newVal);
                NOW_Tool.updataTool();
            });

        });
    }
    /** 初始化所有工具，並設定預覽畫筆及監聽器  */
    public void toolsInitia(GraphicsContext prevGC){

        for(Tool tool:TOOL_MAP.values()){
            tool.setPrevGC(prevGC);
        }

        //預設為選擇器
        TOOL_GROUP.getToggles().get(0).setSelected(true);

        //((TextTool)TOOL_MAP.get("text")).setAPP(APP);
        
        //匿名函式宣告： 監聽 ToolBar 的所有元件，以至於可以更動後更改 Tool
        TOOL_GROUP.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) -> {
            if (new_toggle == null){
                TOOL_GROUP.getToggles().get(0).setSelected(true);
                changTool(ICONS_NAME[0]);
            }
            else{
                changTool(TOOL_GROUP.getSelectedToggle().getUserData().toString());

            }
        });
    }
    
    //===========================================================================
    //========================         功能區        ============================
    /** 改變選擇的工具 
     *  @return {@code true = 更換成功} | {@code false = 更換失敗}*/
    public boolean changTool( String toolName ){

        int dexA, dexB;
        int index = ( dexA = Arrays.asList(ICONS).indexOf(toolName) ) > (dexB = Arrays.asList(ICONS_NAME).indexOf(toolName) ) ? dexA : dexB;
        //if( !TOOL_MAP.containsKey( toolName ) )return false;
        if( index == -1 )return false;

        NOW_Tool = TOOL_MAP.get( ICONS_NAME[index] );
        TOOL_GROUP.selectToggle( TOOL_GROUP.getToggles().get( NOW_Tool.getID() ) );
        
        ScaleTransition scale = new ScaleTransitionBuilder(0.3, (Node)TOOL_GROUP.getSelectedToggle()).setRange(0.85, 1.0).build();
        scale.play();


        Platform.runLater( () -> {
            //清空上方控制項
            APP.DETAIL_PANE.getChildren().clear();
            //將上方控制項設為符合現在的工具
            APP.DETAIL_PANE.getChildren().setAll(NOW_Tool.getDetailPane());
            
            if (!TOOL_MAP.get(ICONS_NAME[0]).equals(NOW_Tool)) {
                NOW_Tool.setMainGC(APP.MAIN.getLayerList().getActiveLayer());
                NOW_Tool.setColor(inSide, outSide);
            }
            NOW_Tool.updataTool();
        } );



        return true;
    }

    /** 取的目前選擇的工具 {@link Tool} @return {@code [Tool]} */
    public Tool getNowTool(){ return NOW_Tool; }


    /**設定顏色
     * @param colorName
     * @return
     * @throws Exception */
    public String setColor(ColorPicker tCP, String colorName) throws Exception {
        Color tColor = Color.web(colorName);

        Platform.runLater(() -> {
            boolean b = false;
            while (!b) {
                b = false;
                try {
                    Thread.sleep(5);

                    tCP.setValue(tColor);

                    if (tCP.equals(INSIDE_COLORPICKER)) {
                        inSide = tCP.getValue();
                        if (!TOOL_MAP.get(ICONS_NAME[0]).equals(NOW_Tool)) {
                            NOW_Tool.setInSideColor(inSide);
                        }
                    } else {
                        outSide = tCP.getValue();
                        if (!TOOL_MAP.get(ICONS_NAME[0]).equals(NOW_Tool)) {
                            NOW_Tool.setOutSideColor(outSide);
                        }
                    }
                    NOW_Tool.updataTool();

                    b = true;
                } catch (Exception e) {
                }
            }
        });
        return "";
    }

    public String setInsideColor(String colorName) throws Exception {
        try {
            setColor(INSIDE_COLORPICKER, colorName);
        } catch (Exception e) {
            throw new Exception("無此顏色");
        }

        return "true";
    }

    public String setOutsideColor(String colorName) throws Exception {
        try {
            setColor(OUTSIDE_COLORPICKER, colorName);
        } catch (Exception e) {
            throw new Exception("無此顏色");
        }

        return "true";
    }
    
}