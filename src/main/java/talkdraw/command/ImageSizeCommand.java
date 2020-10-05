package talkdraw.command;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.imgobj.ImageObject;

/** 【指令】《工具相關指令》
 * <p>圖片旋轉指令</p>
 *  @param args_0 工具名稱 */
public class ImageSizeCommand extends Command{

    /** 建構子 */
    public ImageSizeCommand(App APP, CommandAttribute yamlLoader){
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
                return new NextCommand( this.getClass(), Phase.Get_Value );
            //------------------------
            case Get_Value:
                //判斷輸入位數
                if( args.length < 2 )
                    return new NextCommand( this.getClass(), phase );

                NextCommand nextCommand = doSizeAction( args );
                return (nextCommand != null) ? nextCommand : failure( args );
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "成功設定圖片 " + args[0] + " 大小 " + args[1] + "x" + args[2], App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( args[0] + " 或 " + args[1] + " 不是個有效的數字" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ 
        if( APP.MAIN.isNowHaveSelected() == false )return null;
        if( args.length < 2 )return null;
        return doSizeAction( args ); 
    }


    /** 設定旋轉
     *  @param argVal 數值字串
     *  @return 下個指令*/
    private NextCommand doSizeAction( String ... args ){
        try{
            int val1 = Integer.parseInt( args[0] );
            int val2 = Integer.parseInt( args[1] );
            ImageObject res = APP.MAIN.getNowSelectedImageObject();
            res.setImageSize(val1, val2);
            return success( res.getName(), args[0], args[1] );
        }
        catch(NumberFormatException e){
            return null;
        }
        catch( Exception e ){
            e.printStackTrace();
            return null;
        }
    }
}