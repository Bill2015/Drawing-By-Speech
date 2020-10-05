package talkdraw.command;

import talkdraw.App;
import talkdraw.Main;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;

/** 【指令】《網路相關指令》
    * <p>取得網路的圖片</p>
    * <p>目前未讓使用者選擇</p>
    *  @param args_0 關鍵字 */
public class NewImageFromWebCommand extends Command{

    /** 建構子 */
    public NewImageFromWebCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                return new NextCommand( this.getClass(), Phase.Get_Web_Keyword );
            //------------------------
            case Get_Web_Keyword:
                if( args.length < 1 )
                    return new NextCommand( this.getClass(), phase );

                return getImageFromWeb( args[0] );
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( String.join("","成功增加 ", args[0] ) , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( String.join("","未找到任何關於 ", args[0], " 的圖片") , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

    /** 取得網路上的圖片
     *  @param keyword 欲取得的圖片關鍵字 
     *  @return 回傳下一個狀態 {@code NextCommand}*/
    private NextCommand getImageFromWeb( String keyword ){        
        Main main = APP.MAIN;

        if( main.getNetConnection().searchImages( keyword ) <= 0 ){
            return failure( keyword );
        }
        APP.STATUS_PANE.changeOnlyStatusText("正在取得圖片資訊");
        if( !main.addImageToLayer(Main.GET_MODE.WEB) ){
            return failure( "ID 為 NULL" );
        }
        return success( keyword );
    }
}