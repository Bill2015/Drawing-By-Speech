package talkdraw;

import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

public class DetailPane extends StackPane{
    /** 最大高度 */
    public final int MAX_HEIGHT = 80;

    public DetailPane(){
        setId("tool_detail");
        setPadding( new Insets(10) );
        setMinHeight(0);
        setMaxHeight(MAX_HEIGHT);
        setPrefHeight(MAX_HEIGHT);
    }
}