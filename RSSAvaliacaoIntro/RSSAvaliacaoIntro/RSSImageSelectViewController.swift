//
//  RSSImageSelectViewController.swift
//  RSSAvaliacaoIntro
//
//  Created by Usuário Convidado on 23/02/16.
//  Copyright © 2016 Usuário Convidado. All rights reserved.
//

import UIKit

class RSSImageSelectViewController: UIViewController {
    
    var delegate:RSSImageSelectViewControllerDelegate?
    var fruitNames:[String] = ["abacaxi","banana","cereja","kiwi","laranja","limao","manga","uva"]
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        var x:Int = 20
        let y:Int = 110
        var counter:Int = 1
        var line:Int = 1
        for fruitName:String in fruitNames {
            
            let uiButton:UIButton = UIButton(frame: CGRect(x: CGFloat(x), y: CGFloat(y * line), width: CGFloat(100), height: CGFloat(100)))
            
            uiButton.titleLabel?.text = fruitName
            uiButton.setImage(UIImage(named: fruitName), forState: UIControlState.Normal)
            uiButton.addTarget(self, action: "fruitClick:", forControlEvents: UIControlEvents.TouchUpInside)
            
            self.view.addSubview(uiButton)
            
            if(counter == 2){
                counter = 1
                line += 1
                x = 20
            } else {
                counter += 1
                x = 180
            }
        }
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func fruitClick(caller:UIButton){
        self.delegate?.fruitIsSelected((caller.titleLabel?.text)!)
        self.dismissViewControllerAnimated(true, completion: nil)
    }
}

protocol RSSImageSelectViewControllerDelegate{
    func fruitIsSelected(fruit:String) -> Void
}
