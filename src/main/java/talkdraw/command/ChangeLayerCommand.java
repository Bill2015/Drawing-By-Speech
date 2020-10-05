package talkdraw.command;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.imgobj.Layer;

/**【指令】 《畫布相關指令》
 * <p>改變目前操作的圖層</p> 
 *  <p><b>註：目前上一個與下一個還未製作</b></p>
 *  @param args_0 欲更換圖層的 {@code 名稱}、 {@code 上一個}、{@code 下一個}*/
public class ChangeLayerCommand extends Command{

    /** 建構子 */
    public ChangeLayerCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                return new NextCommand( this.getClass(), Phase.Get_Name );
            //------------------------
            case Get_Name:
                if( args.length < 1 )
                    return new NextCommand( this.getClass(), phase );

                return changeByName( args[0] ) ? success() : failure( args[0] );
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "更換圖層成功" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( "未找到名稱關於 " + args[0] + " 的圖層！" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

    /** 利用名稱更換圖片 
     *  @param name 圖層名稱 */
    private boolean changeByName( String name ){        
        Layer layer = APP.MAIN.getLayerList().getElementByName( name );
        if( layer == null ){ 
            return false;
        }
        APP.MAIN.getLayerList().changeActiveLayer( layer );
        return true;
    }
}