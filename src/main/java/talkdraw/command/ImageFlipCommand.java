package talkdraw.command;

import java.util.ArrayList;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.imgobj.ImageObject;

/** 【指令】《工具相關指令》
 * <p>圖片動作指令</p>
 *  @param args_0 工具名稱 */
public class ImageFlipCommand extends Command{

    private final ArrayList<String> actionList;

    /** 建構子 */
    public ImageFlipCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
        this.actionList = yamlLoader.getArgsList();
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                //假如目前沒有選擇出預設的圖片就導向至 Select Image
                if( APP.MAIN.isNowHaveSelected() == false ){
                    return new NextCommand( SelectImageCommand.class, Phase.Get_Name_Or_Tag, true, false );
                }
                return new NextCommand( this.getClass(), Phase.Get_Action );
            //------------------------
            case Get_Action:
                //判斷輸入位數
                if( args.length < 1 )
                    return new NextCommand( this.getClass(), phase );

                NextCommand nextCommand = filpAction( args[0] );
                return ( nextCommand != null ) ? nextCommand : failure("沒有關於 " + args[0] + " 的動作" );
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( args[0], App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( args[0], App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    /** 在暫存時動作指令 
     *  @param arg 參數*/
    @Override public NextCommand doTemporaryAction( String ... args ){ 
        if( APP.MAIN.isNowHaveSelected() == false )return null;
        return filpAction( args[0] );
    }

    public NextCommand filpAction( String aciton ){
        
        ImageObject imgObj = APP.MAIN.getNowSelectedImageObject();
        switch ( actionList.indexOf( aciton ) ) {
            case 0:
                imgObj.filp( ImageObject.FILP_HORIZONTAL );
                return success("圖片 水平 翻轉");
            case 1:
                imgObj.filp( ImageObject.FILP_VERTICAL );
                return success("圖片 垂直 翻轉");
            default:
                return null;
        }
    }
}