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
import talkdraw.Main;
import talkdraw.componet.ChineseTextField;
import talkdraw.componet.MyLabel;

public class AddImagePane extends Stage{
    
    private String title = "搜尋項目";
    
    public AddImagePane( Window parentStage, App APP, Main.GET_MODE mode){
        
        //永遠在最上層
        setAlwaysOnTop(true);
        initModality(Modality.WINDOW_MODAL);
    

        //建立底層的 Stack Pane
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: transparent;");
        vBox.setPadding(new Insets(30));
        vBox.setAlignment(Pos.CENTER);
        

        //設定中間的文字
        MyLabel titleLabel = new MyLabel( title, 40, Pos.CENTER, true );
        titleLabel.setTextFill( Color.BLACK );
        titleLabel.setStyle("-fx-background-color: #ffffff55;");
        titleLabel.setPrefWidth(40 * (title.length() + 1));

        /** 提供使用者輸入的 TextField */
        ChineseTextField textField = new ChineseTextField("");
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
                if(textField.getText().equals("")) close();

                Main main = APP.MAIN;
                String keyword=textField.getText();
                try{
                    switch(mode){
                        case WEB:
                            if( main.getNetConnection().searchImages( keyword ) <= 0 ){
                                System.out.println(keyword);
                            }
                        break;
                        case LOCAL:
                            if( main.getLocalConnection().searchImages( keyword ) <= 0 ){
                                System.out.println(keyword);
                            }
                        break;
                    }
                    System.out.println(mode.toString());
                    //APP.MSG_PANE.changeOnlyStatusText("正在取得圖片資訊");
                    if( !main.addImageToLayer(mode) ){
                        System.out.println("ID 為 NULL");
                    }
                }catch(Exception error){
                    error.printStackTrace();
                    APP.println("找不到圖片",App.PRINT_BOTH);
                }
                //APP.MSG_PANE.changeBothText("", "指令");
                close();

            }
        });

        vBox.getChildren().addAll( titleLabel,/* textLabel, */textField, button);
        


        initStyle( StageStyle.TRANSPARENT );
        setScene( new Scene( vBox ){ { setFill( Color.rgb(100, 100, 100, 0.5) ); }} );

        

        //鎖住父視窗
        initOwner(parentStage);
    }

}
