package talkdraw.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import talkdraw.componet.MyLabel;

public class InfoStage extends Stage {
    /**
     * 資訊欄
     * @param title 標題
     * @param text 內文
     * @param parentStage 父 {@link Stage}，當這個視窗還沒關掉時會鎖住父視窗
     */
    public InfoStage(String title,String text, Window parentStage){
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

        MyLabel textLabel = new MyLabel( text, 20, Pos.CENTER_LEFT, true );
        textLabel.setTextFill( Color.BLACK );
        textLabel.setPrefWidth(40 * (title.length() + 1)*2);
        

        //設定確認按鈕
        MyLabel button=new MyLabel("確定",30,Pos.CENTER, true);
        button.setFont(new Font("Yu Gothic UI Semibold",30));
        button.setTextFill( Color.BLACK );
        button.setPrefWidth(30 * (title.length() + 1));
        button.getStylesheets().add(getClass().getResource("/styles/toolPane.css").toExternalForm());
        button.getStyleClass().add("infoButton");

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            close();
        });

        vBox.getChildren().addAll(titleLabel, textLabel, button);
        


        initStyle( StageStyle.TRANSPARENT );
        setScene( new Scene( vBox ){ { setFill( Color.rgb(100, 100, 100, 0.5) ); }} );
        //鎖住父視窗
        initOwner(parentStage);
    }
}