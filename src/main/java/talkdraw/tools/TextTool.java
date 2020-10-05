package talkdraw.tools;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import talkdraw.componet.ChineseTextField;
import talkdraw.componet.MyLabel;
import talkdraw.componet.TextSliderBar;
import talkdraw.event.ToolEvent;
import talkdraw.imgobj.LayerClone;

/** <p>繼承了 {@link talkdraw.tools.Tool } </p> 
 *  <p>橢圓形工具</p> */
public class TextTool extends Tool{
    /** 設定文字樣式的 勾勾選項
     * @apiNote [0] = 粗體 
     * @apiNote [1] = 斜體 
     * @apiNote [2] = 底線 
     * @apiNote [3] = 刪除線 
     */
    private CheckBox textStyleCheckBox[]=new CheckBox[4];
    /** 設定文字大小的 TextSliderBar */
    private TextSliderBar textSizeTS;
    /** 設定文字字體的 下拉選單 */
    private ComboBox<Text> textTypeComboBox;
    /** 輸入文字的 ChineseTextField */
    private ChineseTextField textInputField;
    /** 預覽用文字 */
    private Text prevText;
    /** 預覽文字位置 */
    private Pane prevTextPane;
    /** 將主程式參考進來，以方面更動其他物件 */
    //private App APP;
    
    private double x, y;
    /** 建構子
     *  @param name 工具名稱 
     *  @param mainGC 主要畫布畫筆
     *  @param prevGC 預覽畫布畫筆*/
    public TextTool(String name, GraphicsContext mainGC, GraphicsContext prevGC){
        super(name, mainGC, prevGC);
        initialPane();
    }
    //============================================================================================
    //================================     滑鼠功能區     =========================================
    /** 當滑鼠按下時的動作 */
    @Override
	public void onMouseClick(MouseEvent e) {
        
        LayerClone clone=new LayerClone(activeLayer);
        double nowX = e.getX(), nowY = e.getY();
        double centerX = nowX - textSizeTS.getValue() * prevText.getText().getBytes().length / 4,
                centerY = nowY - textSizeTS.getValue() / 2;
        mainGC.drawImage(getImageOnText(prevText), centerX, centerY);

        
        fireEvent(new ToolEvent(ToolEvent.TEXT, "手動繪製", activeLayer, clone));
        //Text newText = cloneText(prevText);

        //將物件加入主畫布
        //APP.DRAW_AREA.getUpponPane().getChildren().add(newText);
        //藉由控制邊距達到設定位置的目的
        //APP.DRAW_AREA.getUpponPane().setMargin(newText, new Insets(nowY, nowY, nowY, nowX));

        
        //APP.DRAW_AREA.getUpponPane().getChildren().remove(prevText);
        //prevTextPane.getChildren().add(prevText);
	}
	@Override
	public void onMouseRelease(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseDragging(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseMove(MouseEvent e) {
        double nowX = e.getX(), nowY = e.getY();
        double centerX = nowX - textSizeTS.getValue() * prevText.getText().getBytes().length / 4,
                centerY = nowY - textSizeTS.getValue() / 2;

        //藉由控制邊距達到設定位置的目的
        //APP.DRAW_AREA.getUpponPane().setMargin(prevText, new Insets(nowY, nowY, nowY, nowX));
        prevGC.drawImage(getImageOnText(prevText), centerX, centerY);
	}
    //============================================================================================
    //=================================     語音設定     ==========================================
    @Override
    public String setValue(String name, String value) throws Exception {
        if(name==null)return "請輸入屬性名稱"; 
        switch(name){
            case "字型大小":
                if(value==null)return "請輸入數字";
                try{
                    textSizeTS.setValue(Integer.parseInt(value));
                }catch(Exception e){
                    return "請輸入數字";
                }
            break;
            case "粗體":
                textStyleCheckBox[0].setSelected(true);
            break;
            case "斜體":
                textStyleCheckBox[1].setSelected(true);
            break;
            case "底線":
                textStyleCheckBox[2].setSelected(true);
            break;
            case "刪除線":
                textStyleCheckBox[3].setSelected(true);
            break;
            case "選擇字體":
                if(value==null){
                    //textTypeComboBox.getContextMenu().show(detailPane, textTypeComboBox.getLayoutX(), detailPane.getHeight());
                    return "請輸入字體名稱或編號";
                }
                for(Text item:textTypeComboBox.getItems()){
                    if(item.getText().split("、")[0].equals( value ) || item.getText().split("、")[1].equals( value )){
                        
                        Platform.runLater(()->{
                            textTypeComboBox.getSelectionModel().select(item);
                        });
                        break;
                    }
                }
                //dashCheckBox.setSelected(!dashCheckBox.isSelected());
            break;
            case "輸入文字":
                if(value==null)return "請輸入文字";
                textInputField.setText(value);
                //if(value.length<1)return false;
                //if(dashCheckBox.isSelected()) dashSizeTS.setValue(value[0]);
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
        double nowX = pos[0], nowY = pos[1];
        double centerX = nowX - textSizeTS.getValue() * prevText.getText().getBytes().length / 4,
                centerY = nowY - textSizeTS.getValue() / 2;
                
        /* Platform.runLater(() -> {  boolean b=false;
            while(!b){
                b=false;
                try{
                    Thread.sleep( 5 );
                    prevGC.drawImage(getImageOnText(prevText), centerX, centerY);
                    b=true;
                }catch(Exception e){ }
                }
                
            });
        
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
                        clearPrevGC();
                        mainGC.drawImage(getImageOnText(prevText), centerX, centerY);
        
                        fireEvent(new ToolEvent(ToolEvent.TEXT, "語音繪製", activeLayer, clone));
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
        setColor(inSide, outSide);

        prevText.setFont(getTextFont());
        prevText.setText(textInputField.getText());
        prevText.setUnderline(textStyleCheckBox[2].isSelected());
        prevText.setStrikethrough(textStyleCheckBox[3].isSelected());
        prevText.setStroke(outSide);
        prevText.setFill(inSide);

    }
    protected void initialPane(){
        detailPane = new Pane();
        textSizeTS = new TextSliderBar(1, 1000, 50, "");
        textStyleCheckBox[0]=new CheckBox("粗體");
        textStyleCheckBox[1]=new CheckBox("斜體");
        textStyleCheckBox[2]=new CheckBox("底線");
        textStyleCheckBox[3]=new CheckBox("刪除線");
        textInputField=new ChineseTextField("TalkDraw");
        prevText=new Text(textInputField.getText());

        prevTextPane= new Pane(prevText);
        prevTextPane.setPrefSize(200, 60);
        prevTextPane.setMaxSize(200, 60);
        prevTextPane.setMinSize(200, 60);
        prevTextPane.setClip(new Rectangle(200, 60));
        prevText.setLayoutY(45);
        
        typeCombInitial();
        

        //detailPane.setStyle("-fx-background-color: #aaa");
        
        // GridPane 排版
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_LEFT);
        gridPane.setHgap(10.0);
        // 控制項
        gridPane.add(new MyLabel("字型大小"), 0, 0);
        gridPane.add(textSizeTS, 1, 0 );
        gridPane.add(new MyLabel("輸入文字"), 0, 1);
        gridPane.add(textInputField, 1,1);
        gridPane.add(textStyleCheckBox[0], 2, 0);
        gridPane.add(textStyleCheckBox[1], 3, 0);
        gridPane.add(textStyleCheckBox[2], 4, 0);
        gridPane.add(textStyleCheckBox[3], 5, 0);
        gridPane.add(new MyLabel("選擇字體"), 2, 1);
        gridPane.add(textTypeComboBox, 3,1,3,1);
        gridPane.add(prevTextPane, 6, 0, 1, 2);

        detailPane.getChildren().add(gridPane);

        initialListener();
    }
    
    protected void initialListener() {
        textSizeTS.getValueProperty().addListener((obser, oldVal, newVal)->{
            prevText.setFont(getTextFont());
        });
        textStyleCheckBox[0].selectedProperty().addListener((obser, oldVal, newVal)->{
            prevText.setFont(getTextFont());
        });
        textStyleCheckBox[1].selectedProperty().addListener((obser, oldVal, newVal)->{
            prevText.setFont(getTextFont());
        });
        textTypeComboBox.valueProperty().addListener((obser, oldVal, newVal)->{
            prevText.setFont(getTextFont());
        });
        textInputField.textProperty().addListener((obser, oldVal, newVal)->{
            prevText.setText(textInputField.getText());
        });
        textStyleCheckBox[2].selectedProperty().addListener((obser, oldVal, newVal) -> {
            prevText.setUnderline(textStyleCheckBox[2].isSelected());
        });
        textStyleCheckBox[3].selectedProperty().addListener((obser, oldVal, newVal) -> {
            prevText.setStrikethrough(textStyleCheckBox[3].isSelected());
        });

        // 匿名函式宣告： 監聽 detailPane 的所有元件，以至於可以更動後更改 Tool
        /*detailPane.getChildren().forEach(node -> {
            node.addEventHandler(Event.ANY, e -> {
                updataTool();
            });
        });*/
    }
    // 初始化 Type ComboBox
    private void typeCombInitial() {
        /** 系統支援的所有字型 */
        ObservableList<String> fontType = FXCollections.observableArrayList(Font.getFamilies());
        //將字型加入選單
        textTypeComboBox = new ComboBox<Text>();
        fontType.forEach(item->{
            //將項目加上編號
            Text t=new Text(fontType.indexOf(item)+"、"+item);
            t.setFont(new Font(item, 11));
            textTypeComboBox.getItems().add(t);
        });
        
        //textTypeComboBox.setPrefSize(100,15);
        //預設字型
        textTypeComboBox.getSelectionModel().select(fontType.indexOf("Arial"));

    }
    /** 取得 文字設定 的值 (包含 {@code 粗體 斜體 字型 大小 }) */
    public Font getTextFont(){ 
        boolean b = textStyleCheckBox[0].isSelected(), i = textStyleCheckBox[1].isSelected();
        //System.out.println(((Text)textTypeComboBox.getSelectionModel().getSelectedItem()).getText().split("、")[1]);
        if (b && i)
            return Font.font(((Text)textTypeComboBox.getSelectionModel().getSelectedItem()).getText().split("、")[1], FontWeight.BOLD,
                    FontPosture.ITALIC, textSizeTS.getValue());
        else if (b)
            return Font.font(((Text)textTypeComboBox.getSelectionModel().getSelectedItem()).getText().split("、")[1], FontWeight.BOLD,
                    textSizeTS.getValue());
        else if (i)
            return Font.font(((Text)textTypeComboBox.getSelectionModel().getSelectedItem()).getText().split("、")[1], FontPosture.ITALIC,
                    textSizeTS.getValue());
        else
            return Font.font(((Text)textTypeComboBox.getSelectionModel().getSelectedItem()).getText().split("、")[1], textSizeTS.getValue());
    }
    /*public void setAPP(App APP){
        //this.APP=APP;
        
        APP.DRAW_AREA.getUpponPane().addEventHandler(MouseEvent.MOUSE_ENTERED,e->{
            if(APP.TOOL_BAR.NOW_Tool.equals(this)){
                //將物件加入主畫布
                APP.DRAW_AREA.getUpponPane().getChildren().add(prevText);
            }
        });
        APP.DRAW_AREA.getUpponPane().addEventHandler(MouseEvent.MOUSE_EXITED,e->{
            if(APP.TOOL_BAR.NOW_Tool.equals(this)){
                //將物件移出主畫布
                APP.DRAW_AREA.getUpponPane().getChildren().remove(prevText);
                //將物件放回預覽位置
                prevTextPane.getChildren().add(prevText);
                prevText.setLayoutX(0);prevText.setLayoutY(45);
            }
        });
    }*/
    /* private Text cloneText(Text text){
        Text copy=new Text();

        copy.setFont(prevText.getFont());
        copy.setText(prevText.getText());
        copy.setUnderline(prevText.isUnderline());
        copy.setStrikethrough(prevText.isStrikethrough());
        copy.setStroke(prevText.getStroke());
        copy.setFill(prevText.getFill());

        return copy;
    } */
    private Image getImageOnText(Text input){
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill( Color.TRANSPARENT );
        /*ImageView view = new ImageView( prevText.snapshot(parameters, null) );
        return view.getImage();*/
        return prevText.snapshot(parameters, null);
    }

}