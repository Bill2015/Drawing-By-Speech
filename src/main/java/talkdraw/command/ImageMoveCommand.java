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
public class ImageMoveCommand extends Command{

    private final ArrayList<String> actionList;

    /** 動作的位數 */
    private int actionIndex = 0;

    /** 一點點的間隔 */
    private final int LITTLE_GAP = 10;
    /** 很多的間隔 */
    private final int A_LOT_GAP = 50;

    /** 建構子 */
    public ImageMoveCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
        this.actionList = yamlLoader.getArgsList();
    }
    @Override
    public NextCommand execute( String ... args ) {
        NextCommand nextCommand;
        switch ( phase ) {
            case None:
                //假如目前沒有選擇出預設的圖片就導向至 Select Image
                if( APP.MAIN.isNowHaveSelected() == false ){
                    return new NextCommand( SelectImageCommand.class, Phase.Get_Name_Or_Tag, true, false );
                }
                return new NextCommand( this.getClass(), Phase.Get_DIRECT );
            //------------------------
            case Get_DIRECT:
                //判斷輸入位數
                if( args.length < 1 )
                    return new NextCommand( this.getClass(), phase );

                if( args.length == 1 ){
                    nextCommand = actionByValue( args[0] );
                    if( nextCommand != null )return nextCommand;
                    else{
                        nextCommand = judgeAction( args[0] );
                        return (nextCommand != null) ? nextCommand : failure("沒有關於 " + args[0] + " 的動作");
                    }
                }
                //如果兩個位數
                else if( args.length >= 2 ){
                    if( judgeAction( args[0] ) == null ){
                        return failure("沒有關於 " + args[0] + " 的動作");
                    }
                    nextCommand = actionByValue( args[1] );
                    return (nextCommand != null) ? nextCommand : failure( args[1] + " 不是個有效的數字" ) ;
                }

            case Get_Value:
                if( args.length < 2 )
                    return new NextCommand( this.getClass(), phase );

                nextCommand = actionByValue( args[1] );
                return (nextCommand != null) ? nextCommand : failure( args[1] + " 不是個有效的數字" ) ;
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "圖片" + args[0] + " 個像素", App.PRINT_BOTH);
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( args[0], App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override public NextCommand doTemporaryAction( String ... args ){ 
        NextCommand nextCommand;
        if( APP.MAIN.isNowHaveSelected() == false )return null;
        if( args.length == 1 ){
            nextCommand = actionByValue( args[0] );
            if( nextCommand != null )return nextCommand;
            else{
                return judgeAction( args[0] );
            }
        }
        //如果兩個位數
        else if( args.length >= 2 ){
            if( judgeAction( args[0] ) == null ){
                return failure("沒有關於 " + args[0] + " 的動作");
            }
            return actionByValue( args[1] );
        }
        return null;
    }

    /** 判斷動作 
     *  @param action 動作
     *  @return 下個指令 {@code [NextCommand]}*/
    private NextCommand judgeAction( String action ){
        actionIndex = actionList.indexOf( action );
        switch ( actionIndex ) {
            //--圖片[向上移動]動作--------------------------
            case 0:
            //--圖片[向下移動]動作--------------------------
            case 1:
            //--圖片[向左移動]動作--------------------------
            case 2:
            //--圖片[向右移動]動作--------------------------
            case 3:
                return new NextCommand( getClass() , Phase.Get_Value);
            //---------------------------------------------
            default: return null;
        }
    }
    /** 執行動作 
     *  @param args 動作數值
     *  @return 下個指令 {@code [NextCommand]}*/
    private NextCommand actionByValue( String action ){
        ImageObject imgObj = APP.MAIN.getNowSelectedImageObject();
        try{
            int value = 0;
            //移動一點點
            if( action.equals( actionList.get(4) ) ){
                value =  LITTLE_GAP;
            }
            //移動很多
            else if( action.equals( actionList.get(5) ) ){
                value =  A_LOT_GAP;
            }
            else value  = Integer.parseInt( action );
            switch ( actionIndex ) {
                //--圖片[向上移動]動作---------------------------------------------------------
                case 0:
                    imgObj.move( ImageObject.MOVE_UP , value );
                    return success("向上移動 " + value );
                //--圖片[向下移動]動作---------------------------------------------------------
                case 1:
                    imgObj.move( ImageObject.MOVE_DOWN , value );
                    return success("向下移動 " + value );
                //--圖片[向左移動]動作---------------------------------------------------------
                case 2:
                    imgObj.move( ImageObject.MOVE_LEFT , value );
                    return success("向左移動 " + value );
                //--圖片[向右移動]動作---------------------------------------------------------
                case 3:
                    imgObj.move( ImageObject.MOVE_RIGHT , value );
                    return success("向右移動 " + value );
                default: return null;
            }
        }
        catch( NumberFormatException e ){
            return null;
        }
        catch( Exception e ){
            e.printStackTrace();
            return null;
        }
    }

}