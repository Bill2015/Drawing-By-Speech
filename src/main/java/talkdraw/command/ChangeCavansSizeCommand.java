package talkdraw.command;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;

/**【指令】 《畫布相關指令》
    * <p>設定畫布大小 輸入寬與高</p>
    *  @param args_0 寬
    *  @param args_1 高 */
public class ChangeCavansSizeCommand extends Command{

    /** 建構子 */
    public ChangeCavansSizeCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                return new NextCommand( this.getClass(), Phase.Get_Value );
            //------------------------
            case Get_Value:
                if( args.length < 2 )
                    return new NextCommand( this.getClass(), phase );

                return changeCanvasSize( args[0], args[1] );
            //------------------------
            default:break;
        }

        return null;
    }
    @Override protected NextCommand success( String ... args ) {
        APP.println( "畫布大小更換成功" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override protected NextCommand failure( String ... args ) {
        APP.println( "畫布的大小只能界在 1 ~ 2048 之間 !" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

    /** 取得網路上的圖片
     *  @param w 欲設定的寬
     *  @param h 欲設定的高
     *  @return 回傳下一個狀態 {@code NextCommand}*/
    private NextCommand changeCanvasSize( String w, String h ){        

        int width, height;
        try{ width = Integer.parseInt( w ); height = Integer.parseInt( h ); }
        catch( Exception e ){ return failure(); }

        if( width <= 0 || width > App.MAX_DRAW_WIDTH || height <= 0 || height > App.MAX_DRAW_HEIGHT )
            return failure();
            
        APP.DRAW_AREA.reSizeDrawArea( width, height );

        return success();
    }
}