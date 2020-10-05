package talkdraw.command;

import java.util.ArrayList;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.imgobj.ImageObject;

/** 【指令】《工具相關指令》
 * <p>圖片旋轉指令</p>
 *  @param args_0 工具名稱 */
public class ImageViewOrderCommand extends Command{

    private final ArrayList<String> actionList;

    /** 建構子 */
    public ImageViewOrderCommand(App APP, CommandAttribute yamlLoader){
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

                NextCommand nextCommand = doViewOrderAction( args[0] );    
                return (nextCommand != null) ? nextCommand : failure( args );
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "圖片 " + args[0] , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( "沒有關於 " + args[0] + " 的動作" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ 
        if( APP.MAIN.isNowHaveSelected() == false )return null;
        return doViewOrderAction( args[0] ); 
    }

    /** 執行動作圖片順序 
     *  @param action 動作
     *  @return 下個指令 {@code [NextCommand]}*/
    private NextCommand doViewOrderAction( String action ){
        ImageObject imgObj = APP.MAIN.getNowSelectedImageObject();

        //圖片至頂
        if( action.equals( actionList.get(0) )  ){
            imgObj.setViewOrder( ImageObject.ORDER_TOP );
            return success("至頂");
        }
        //圖片至底
        else if( action.equals( actionList.get(1) ) ){
            imgObj.setViewOrder( ImageObject.ORDER_BOTTOM );
            return success("至底");
        }
        //圖片向上一層
        else if( action.equals( actionList.get(2) ) ){
            int val = imgObj.setViewOrder( ImageObject.ORDER_UP );
            return success( (val == 0) ? "向上一層" : "以達到頂" );
        }
        //圖片向下一層
        else if( action.equals( actionList.get(3) ) ){
            int val = imgObj.setViewOrder( ImageObject.ORDER_DOWN );
            return success( (val == 0) ? "向下一層" : "以達到底" );
        }
        else return null;
    }
}