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
public class ImageAlignCommand extends Command{

    private final ArrayList<String> actionList;

    /** 建構子 */
    public ImageAlignCommand(App APP, CommandAttribute yamlLoader){
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

                NextCommand nextCommand = doAlignAction( args[0] );
                return (nextCommand != null) ? nextCommand : failure();
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "圖片" + args[0]  + "對齊" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( "沒有此動作", App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ 
        if( APP.MAIN.isNowHaveSelected() == false )return null;
        return doAlignAction( args[0] );
    }

    /** 判斷動作 
     *  @param action 動作
     *  @return 下個指令 {@code [NextCommand]}*/
    private NextCommand doAlignAction( String action ){
        //==============================================================================
        ImageObject imgObj = APP.MAIN.getNowSelectedImageObject();
        switch ( actionList.indexOf( action ) ) {
            //--圖片[向上對齊]動作--------------------------
            case 0:
                imgObj.setAlign( ImageObject.ALIGN_TOP );
                return success("向上");
            //--圖片[向下對齊]動作--------------------------
            case 1:
                imgObj.setAlign( ImageObject.ALIGN_DOWN );
                return success("向上");
            //--圖片[向左對齊]動作--------------------------
            case 2:
                imgObj.setAlign( ImageObject.ALIGN_LEFT );
                return success("向上");
            //--圖片[向右對齊]動作--------------------------
            case 3:
                imgObj.setAlign( ImageObject.ALIGN_RIGHT );
                return success("向上");
            //--圖片[置中對齊]動作--------------------------
            case 4:
                imgObj.setAlign( ImageObject.ALIGN_CENTER );
                return success("向上");
            //---------------------------------------------
            default:return null;
        }
    }
}