package talkdraw.command;

import java.util.ArrayList;

import javafx.application.Platform;
import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.imgobj.ImageList;
import talkdraw.imgobj.Layer;

/**【指令】 《重作相關指令》
    * <p>復原下一步的動作</p>*/
public class LayerClearCommand extends Command{

    private final ArrayList<String> actionList;

    /** 建構子 */
    public LayerClearCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
        this.actionList = yamlLoader.getArgsList();
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                return new NextCommand( this.getClass(), Phase.Get_Comfirm);
            //------------------------
            //確定是否要清空畫布
            case Get_Comfirm:
                if( args[0].equals( actionList.get(0) ) ){
                    Layer activeLayer = APP.MAIN.getLayerList().getActiveLayer(); 
                    Platform.runLater(() -> {
                        //清空在 Main 裡的 ImageList
                        APP.MAIN.getImageListMap().replace( activeLayer.getID() , new ImageList() );
                        activeLayer.clearAll();
                    });
                    return success();
                }
                else return failure();
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "成功清空圖層" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( "清空圖層取消" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

}