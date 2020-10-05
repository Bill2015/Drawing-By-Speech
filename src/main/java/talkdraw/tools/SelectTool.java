package talkdraw.tools;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import talkdraw.App;
import talkdraw.componet.TextSliderBar;

/** <p>繼承了 {@link talkdraw.tools.Tool } </p> 
 *  <p>調整畫布大小的工具(可能增加選擇的功能)</p> */
public class SelectTool extends Tool{
    /** 設定畫布 寬 高 的 TextSliderBar */
    private TextSliderBar drawAreaBox[] = new TextSliderBar[2];
    /** 將主程式參考進來，以方面更動其他物件 */
    private final App APP;

    /** 建構子
     *  @param name 工具名稱 
     *  @param APP 選擇*/
    public SelectTool(String name, App APP){
        super( name );
        this.APP = APP;
        initialPane();
    }
    //============================================================================================
    //================================     滑鼠功能區     =========================================
    /** 當滑鼠按下時的動作 */
    @Override
	public void onMouseClick(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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
            case "畫布寬度":
                if(value==null)return "請輸入數字";
                drawAreaBox[0].setValue(Integer.parseInt(value));
            break;
            case "畫布高度":
                if(value==null)return "請輸入數字";
                drawAreaBox[1].setValue(Integer.parseInt(value));
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
        return "true";

    }
    //============================================================================================
    //====================================     雜項     ==========================================
	
    @Override
    public void updataTool() {
       // APP.DRAW_AREA.reSizeDrawArea((int)drawAreaBox[0].getValue(), (int)drawAreaBox[1].getValue());
    }
    protected void initialPane(){
        detailPane = new Pane();
        drawAreaBox[0]=new TextSliderBar(1,App.MAX_DRAW_WIDTH,500,"畫布寬度");
        drawAreaBox[1]=new TextSliderBar(1,App.MAX_DRAW_HEIGHT,500,"畫布高度");
        
        
        // GridPane 排版
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_LEFT);
        gridPane.setHgap(10.0);
        // 控制項
        gridPane.add(drawAreaBox[0],0,0);
        gridPane.add(drawAreaBox[1],1,0);
        
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
        drawAreaBox[0].getSlider().setOnMouseReleased(e->{
            APP.DRAW_AREA.reSizeDrawArea((int)drawAreaBox[0].getValue(), (int)drawAreaBox[1].getValue());
        } );
        drawAreaBox[1].getSlider().setOnMouseReleased(e->{
            APP.DRAW_AREA.reSizeDrawArea((int)drawAreaBox[0].getValue(), (int)drawAreaBox[1].getValue());
        } );
        drawAreaBox[0].addEventHandler(KeyEvent.KEY_RELEASED, e->{
            if(e.getCode()==KeyCode.ENTER)
                APP.DRAW_AREA.reSizeDrawArea((int)drawAreaBox[0].getValue(), (int)drawAreaBox[1].getValue());
        });
        drawAreaBox[1].addEventHandler(KeyEvent.KEY_RELEASED, e->{
            if(e.getCode()==KeyCode.ENTER)
                APP.DRAW_AREA.reSizeDrawArea((int)drawAreaBox[0].getValue(), (int)drawAreaBox[1].getValue());
        });
        // 匿名函式宣告： 監聽 detailPane 的所有元件，以至於可以更動後更改 Tool
        /*
         * detailPane.getChildren().forEach(node->{ node.addEventHandler( Event.ANY, e
         * -> { updataTool(); } ); });
         */

    }
    public void setDrawAreaBox(int width,int height){
        drawAreaBox[0].setValue(width);
        drawAreaBox[1].setValue(height);
    }

}