package talkdraw.command;


import talkdraw.App;
import talkdraw.Main;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.event.ImageEvent;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.ImageObjectClone;
import talkdraw.imgobj.Layer;

/** 【指令】《網路相關指令》
    * <p>取得網路的圖片</p>
    * <p>目前未讓使用者選擇</p>
    *  @param args_0 關鍵字 */
public class NewImageNaturalCommand extends Command{

    private final int BASE_X = 200, BASE_Y = 200;
    private final int OBJECT_OFFECT = 30;
    /** 建構子 */
    public NewImageNaturalCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                return new NextCommand( this.getClass(), Phase.Get_Natural_String );
            //------------------------
            case Get_Natural_String:
                
                return getImageFromWeb( args );
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( String.join("","成功增加場景" ) , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( String.join("","未找到任何關於 ", args[0], " 的圖片") , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

    /** 取得網路上的圖片
     *  @param keyword 欲取得的圖片關鍵字 
     *  @return 回傳下一個狀態 {@code NextCommand}*/
    private NextCommand getImageFromWeb( String ... args ){

        Main main = APP.MAIN;
        ImageObject baseImg = null;
        //各個位置的座標計數
        int posCount[] = new int[]{0,0,0,0};
        //設定狀態
        APP.STATUS_PANE.changeOnlyStatusText("正在執行自然語言");

        //目前選擇的圖層
        Layer activeLayer = main.getLayerList().getActiveLayer();
        //取得基底物件
        if( main.getNetConnection().searchImages( args[0] ) <= 0 ){
            return failure( args[0] );
        }       
        if( (baseImg = main.getNetConnection().createImageObject()) == null ){
            return failure( args[0] );
        } 
        //添加到畫布裡
        ImageObjectClone clone = new ImageObjectClone( baseImg );
        baseImg.setLocation( BASE_X, BASE_Y );
        baseImg.setBelongLayer( activeLayer ); 
        activeLayer.getImageList().add( baseImg );
        activeLayer.fireEvent( new ImageEvent(ImageEvent.ADD, baseImg, clone) );
        main.setNowSelectedImageObject( baseImg );

        //取得基底圖片的寬度與長度
        double bw = baseImg.getImageWidth() / 2, bh = baseImg.getImageHeight() / 2;

        //取得其他物件
        for( int i = 1; i < args.length; i += 2 ){
            if( main.getNetConnection().searchImages( args[i] ) <= 0 )return failure( args[i] );
            ImageObject imgObj = main.getNetConnection().createImageObject();
            ImageObjectClone cloneObj = new ImageObjectClone( imgObj );
            if( imgObj == null )return failure( args[i] );
            switch ( args[ i + 1 ] ) {
                case "上":
                    imgObj.setLocation( BASE_X + posCount[0], BASE_Y - bh );
                    posCount[0] += OBJECT_OFFECT;
                    break;
                case "下":
                    imgObj.setLocation( BASE_X + posCount[1] , BASE_Y + bh );
                    posCount[1] += OBJECT_OFFECT;
                    break;
                case "左":
                    imgObj.setLocation( BASE_X - bw + posCount[2], BASE_Y + bh);
                    posCount[2] += OBJECT_OFFECT;
                    break;
                case "右":
                    imgObj.setLocation( BASE_X + (bw * 2) + posCount[3], BASE_Y + bh );
                    posCount[3] += OBJECT_OFFECT;
                    break;
                default:
                    return failure();
            }
            //設定圖片大小
            imgObj.setImageSize( bw , bh );

            //添加到畫布裡
            imgObj.setBelongLayer( activeLayer );
            activeLayer.getImageList().add( imgObj );
            activeLayer.fireEvent( new ImageEvent(ImageEvent.ADD, imgObj, cloneObj) );
            main.setNowSelectedImageObject( imgObj );
        }
        return success();
    }
}