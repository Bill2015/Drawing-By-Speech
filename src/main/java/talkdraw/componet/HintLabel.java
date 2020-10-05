package talkdraw.componet;

import javafx.scene.control.MenuItem;
import talkdraw.command.base.Command;


public final class HintLabel extends MenuItem{
    final private Command cmd;
    
    public HintLabel( Command cmd ){
        super( cmd.getChineseName() );
        this.cmd = cmd;
        //setFont( new Font("微軟正黑體", 16) );
    }
    /** 取得這個 HintLabel 的中文名稱 */
    final public String getChineseName(){ return cmd.getChineseName(); }

    /** 判斷是否有包含這個指令 與 種類 (ALWAYS 會自己判斷)  
	 *  @param str 使用者輸入進來的 {@code String} (Text Area) 
	 *  @param type 判斷目前的指令的種類
	 *  @return 回傳是否吻合 {@code true} 包含字串且同種類 || {@code false} 不包含字串且同種類 */
	final public boolean isContain( String str ){

		//假如輸入字串與比對字串有相似 或者 輸入 "help" 與 "?" 就成立
		if( cmd.getChineseName().contains(str) || cmd.getEnglishName().contains(str) || str.equalsIgnoreCase("help") || str.equals("?") ){
			setVisible(true);
			return true;
		}
		setVisible(false);
		return false;
	}
}
