package talkdraw.componet;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/** 自訂義 Label 為了可以設定 Font 的 Style */
public class MyLabel extends Label{
	//========================================================================================
	/**自訂義的 JLabel 建構子(為了讓他的字體是 "微軟正黑體") 預設字體 12 且 無粗體 
	 * @param text                JLabel 顯示的字串 */
	public MyLabel(){
		setFont( new Font("微軟正黑體", 12) );
    }
    //========================================================================================
	/**自訂義的 JLabel 建構子(為了讓他的字體是 "微軟正黑體") 預設字體 13 且 無粗體 
	 * @param text                JLabel 顯示的字串 */
	public MyLabel(String text){
		super(text);
		setFont( new Font("微軟正黑體", 13) );
    }
    //========================================================================================
	/**自訂義的 JLabel 建構子(為了讓他的字體是 "微軟正黑體") 預設字體 13 且 無粗體 
	 * @param text  Label 顯示的字串
	 * @param pos   對齊方式  */
	public MyLabel(String text, Pos pos){
        super(text);
        setFont( new Font("微軟正黑體", 13) );
        setAlignment( pos );
	}
	//========================================================================================
	/**自訂義的 JLabel 建構子(為了讓他的字體是 "微軟正黑體") 預設字體 13 且 無粗體 
	 * @param text  Label 顯示的字串
	 * @param fontSize 字體大小 */
	public MyLabel(String text, int fontSize){
        super(text);
        setFont( new Font("微軟正黑體", fontSize) );
	}
	//========================================================================================
	/**自訂義的 JLabel 建構子(為了讓他的字體是 "微軟正黑體") 預設字體 13 且 無粗體 
	 * @param text  Label 顯示的字串
	 * @param fontSize 字體大小 
	 * @param fontColor 字體顏色*/
	public MyLabel(String text, int fontSize, Color color){
        this(text, fontSize);
		setFont( new Font("微軟正黑體", fontSize) );
		setTextFill( color );
	}
	//========================================================================================
	/**自訂義的 JLabel 建構子(為了讓他的字體是 "微軟正黑體") 預設字體 13 且 無粗體 
	 * @param text  Label 顯示的字串
	 * @param fontSize 字體大小 
	 * @param fontColor 字體顏色
	 * @param css 	Css Style*/
	public MyLabel(String text, int fontSize, Color color, String css){
        this(text, fontSize);
		setFont( new Font("微軟正黑體", fontSize) );
		setTextFill( color );
		setStyle(css);
	}
	//========================================================================================
	/** 自訂義的 JLabel 建構子(為了讓他的字體是 "微軟正黑體") 無粗體 
	 *  @param text     JLabel 顯示的字串
	 *  @param fontSize 字體大小
	 *  @param pos      對齊方式 
	 *  @param bold 	是否要粗體*/
	public MyLabel(String text, int fontSize, Pos pos){
		this(text, pos);
		setFont( new Font("微軟正黑體", fontSize) );
	}
	//========================================================================================
	/** 自訂義的 JLabel 建構子(為了讓他的字體是 "微軟正黑體") 
	 *  @param text     JLabel 顯示的字串
	 *  @param fontSize 字體大小
	 *  @param pos      對齊方式 
	 *  @param bold 	是否要粗體*/
	public MyLabel(String text, int fontSize, Pos pos, boolean bold){
		this(text, fontSize, pos);
		setFont( new Font("微軟正黑體", fontSize) );
		setStyle("-fx-font-weight: bold;");
	}
	//========================================================================================
	/** 設定字體大小 
	 *  @param size 字體大小*/
	public void setFontSize(int size){
		setFont( new Font("微軟正黑體", size) );
	}
}