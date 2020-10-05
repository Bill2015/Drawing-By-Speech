package talkdraw.command;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;

/** 【指令】《工具相關指令》
 * <p>設定要使用的工具</p>
 *  @param args_0 工具名稱 */
public class ChangeToolCommand extends Command{

    

    /** 建構子 */
    public ChangeToolCommand(App APP, CommandAttribute yamlLoader){
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

                if( !APP.TOOL_BAR.changTool( args[0].toLowerCase() ) )return failure( args ); 

                return success( args );
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "工具切換成 " + args[0] , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( "找不到工具 " + args[0] , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ 
        if( !APP.TOOL_BAR.changTool( args[0].toLowerCase() ) )return NextCommand.DO_NOTHING; 
        return success( args );
    }

}