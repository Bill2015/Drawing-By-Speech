package talkdraw.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import talkdraw.App;
import talkdraw.componet.ChineseTextField;
import talkdraw.componet.MyLabel;
import talkdraw.connection.LocalConnection;

public class SavetoObjPane extends Stage{
    
    private String title = "存為物件";
    
    public SavetoObjPane( Window parentStage,App APP){
        
        //永遠在最上層
        setAlwaysOnTop(true);
        initModality(Modality.WINDOW_MODAL);
    

        //建立底層的 Stack Pane
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: transparent;");
        vBox.setPadding(new Insets(30));
        vBox.setAlignment(Pos.CENTER);
        

        
        /** 提供使用者輸入的 TextField */
        ChineseTextField textField = new ChineseTextField("命名(標籤1-標籤2-標籤3)");
        textField.setFont(new Font(40));

        //設定確認按鈕
        MyLabel button=new MyLabel("確定",30,Pos.CENTER, true);
        button.setFont(new Font("Yu Gothic UI Semibold",30));
        button.setTextFill( Color.BLACK );
        button.setPrefWidth(30 * (title.length() + 1));
        button.getStylesheets().add(getClass().getResource("/styles/toolPane.css").toExternalForm());
        button.getStyleClass().add("infoButton");

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            textField.fireEvent(new KeyEvent(KeyEvent.KEY_RELEASED, "enter", "enter", KeyCode.ENTER, false, false, false, false));
        });
        textField.addEventHandler(KeyEvent.KEY_RELEASED, e->{
            if(e.getCode() == KeyCode.ENTER && textField.isSendOut() ){
                String name = textField.getText();
                LocalConnection.USED_MODE mode = APP.MAIN.getLocalConnection().getMode();
                if(mode == LocalConnection.USED_MODE.混合){
                    if( name.contains("臉") || name.contains("頭") || name.contains("耳") || name.contains("鼻") || name.contains("髮") || name.contains("眼") || name.contains("嘴") || name.contains("眉")){
                        APP.FILE_PROCCESSOR.SaveAsPNG( LocalConnection.USED_MODE.人臉.path ,textField.getText());
                    }else{
                        APP.FILE_PROCCESSOR.SaveAsPNG( LocalConnection.USED_MODE.一般.path ,textField.getText());
                    }
                }else{
                    APP.FILE_PROCCESSOR.SaveAsPNG( mode.path ,textField.getText());
                }
                close();
            }
        });

        vBox.getChildren().addAll(/* titleLabel, textLabel, */textField, button);
        


        initStyle( StageStyle.TRANSPARENT );
        setScene( new Scene( vBox ){ { setFill( Color.rgb(100, 100, 100, 0.5) ); }} );



        //鎖住父視窗
        initOwner(parentStage);
    }

}
