package talkdraw.command;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;

/**【指令】 《畫布相關指令》
    * <p>設定畫布大小 輸入寬與高</p>
    *  @param args_0 寬
    *  @param args_1 高 */
public class SetInsideColorCommand extends Command{

    /** 建構子 */
    public SetInsideColorCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                //APP.INFO_PANE.println( APP.TOOL_BAR.getNowTool().setValue() );
                return new NextCommand( this.getClass(), Phase.Get_Color );
            //------------------------
            case Get_Color:
                try{
                    String msg=APP.TOOL_BAR.setInsideColor(args[args.length-1]);
                    if( !msg.equals("true") ){
                        APP.INFO_PANE.println( msg );
                        return new NextCommand( this.getClass(), phase );
                    }
                    return success();
                }catch(Exception e){return failure(e.getMessage());}
            //------------------------
            default:break;
        }

        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.INFO_PANE.println( "已設定" );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.INFO_PANE.println( args[0] );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

}