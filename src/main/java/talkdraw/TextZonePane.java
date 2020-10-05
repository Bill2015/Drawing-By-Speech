package talkdraw;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.StackPane;
import talkdraw.command.base.Command;
import talkdraw.componet.ChineseTextField;
import talkdraw.componet.HintLabel;

public class TextZonePane extends StackPane{
    /** 使用者輸入區 */
    private ChineseTextField textField;
    /** 輸入指令提示視窗 */
    private ContextMenu hintPopMenu;
    /** 提示 HintLabel */
    private HintLabel HintLabels[]; 

    private String nowText = "";
    //-------------------------------------------------------------
    /** 所有指令數量 */
    public final int COMMAND_COUNT;
    /** 設定初始大小 */
    public final int MAX_WIDTH = 1200, MAX_HEIGHT = 15;
    /** 將主程式參考進來，以方面更動其他物件 */
    private final App APP;
    public TextZonePane(App APP) {
        this.APP = APP;
        this.COMMAND_COUNT = APP.getCommandSet().size();

        //設定 TextField 的初始設定
        initialTextField();

        //設定 Hint Pop Menu 的初始設定
        initialPopHintMenu();
        
        getChildren().add( textField );
        
        setStyle("-fx-background-color: red");
        setMinHeight( 0 );
        setMinWidth( 0 );
        setPrefWidth(MAX_WIDTH);
    }
    //------------------------------------------------------------------------------
    /** TextField 的初始化 */
    private void initialTextField(){
        textField = new ChineseTextField();
        textField.setId("inputText");
        textField.getStyleClass().setAll("null");

        //當有輸入值時
        textField.setOnKeyReleased(e ->{
            String temp = textField.getText();

            switch ( e.getCode() ) {
                //當按下 Enter 鍵時
                case ENTER:
                    if( !hintPopMenu.isShowing() && textField.isSendOut() ){
                        nowText = textField.getText();
                        textField.setText("");
                    }
                    break;
                
                //當按下方向鍵上下時
                case KP_UP:case KP_DOWN:case DOWN:case UP: break;

                //當按下注音鍵或英文字母時
                default:
                    if( !temp.isEmpty() ){
                        hintPopMenu.hide();                   //先隱藏
                        for( int i = 0; i < COMMAND_COUNT; i++){
                            HintLabels[i].isContain( temp );
                        }
                        hintPopMenu.show( textField, Side.TOP, 0, -2 ); //顯示
                    }
                    break;
            }
        });

    }
    //------------------------------------------------------------------------------
    /** 判斷是否有輸入
     *  @return {@code true = 有輸入} | {@code false = 無輸入} {@code [Boolean]}*/
    public boolean hasInput(){ return !nowText.isBlank(); }

    /** <p>取得從 {@link TextPane} 傳出的字串</p> 
     *  <p>也就是取得使用者的 {@code 手動輸入}</p>
     *  @return 為分割的 輸入字串 {@code [List<String>]}*/
    public List<String> getTextInput(){
        String temp[] = new String[ nowText.split(" ").length ];
        if( temp.length > 1 ){
            temp = nowText.split(" ");
        }
        else temp[0] = nowText;
        nowText = "";
        return Arrays.asList( temp );
    }
    //------------------------------------------------------------------------------
    /** 提示 Pop Menu 初始化 */
    private void initialPopHintMenu(){
        
        HintLabels = new HintLabel[ COMMAND_COUNT ];

        //取得指令迭帶器
        Iterator<Command> cmd = APP.getCommandSet().values().iterator();
        for( int i = 0; i < COMMAND_COUNT; i++ ){
            HintLabels[i] = new HintLabel( cmd.next() );
            //匿名函式宣告： 當使用者按下 指令選擇清單時
            final int index = i;

            //當按下時，設定中文字至 TextField
            HintLabels[i].addEventHandler( ActionEvent.ACTION, e -> {
                textField.setText( HintLabels[index].getChineseName() );
                
                //設定滑鼠光標至尾部
                textField.positionCaret( textField.getText().length() );
            });
        }
        hintPopMenu = new ContextMenu( HintLabels );
        hintPopMenu.setId("hintText");
        hintPopMenu.getStyleClass().clear();
    }
    /** 真正的Visble */
    public void setShow(boolean value){
        if(value) getChildren().add( textField );
        else getChildren().remove(textField);
        setVisible(value);
    }
}


