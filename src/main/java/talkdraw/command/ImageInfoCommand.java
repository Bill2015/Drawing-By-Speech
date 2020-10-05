package talkdraw.command;


import javafx.application.Platform;
import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.imgobj.ImageObject;

/** 【指令】《工具相關指令》
 * <p>圖片動作指令</p>
 *  @param args_0 工具名稱 */
public class ImageInfoCommand extends Command{

    /** 建構子 */
    public ImageInfoCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                //假如目前沒有選擇出預設的圖片就導向至 Select Image
                if( APP.MAIN.isNowHaveSelected() == false ){
                    return new NextCommand( SelectImageCommand.class, Phase.Get_Name_Or_Tag, true, false );
                }
                ImageObject nowImg = APP.MAIN.getNowSelectedImageObject();
                Platform.runLater(() -> nowImg.showInfoList( nowImg.getClickableImageView() ) );
                return success( nowImg.getName() );
    
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "查看圖片 " + args[0] + " 的資訊", App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( args[0], App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    /** 在暫存時動作指令 
     *  @param arg 參數*/
    @Override public NextCommand doTemporaryAction( String ... args ){ 
        return null;
    }
}