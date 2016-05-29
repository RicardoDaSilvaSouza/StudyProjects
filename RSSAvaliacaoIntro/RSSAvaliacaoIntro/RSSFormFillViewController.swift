//
//  RSSFormFillViewController.swift
//  RSSAvaliacaoIntro
//
//  Created by Usuário Convidado on 23/02/16.
//  Copyright © 2016 Usuário Convidado. All rights reserved.
//

import UIKit

class RSSFormFillViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var tfName: UITextField!
    @IBOutlet weak var tfEmail: UITextField!
    @IBOutlet weak var tfPhoneNumber: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tfName.delegate = self
        tfEmail.delegate = self
        tfPhoneNumber.delegate = self
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func tapNext(sender: UIButton) {
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        if(textField == tfName){
            tfEmail.becomeFirstResponder()
        } else if(textField == tfEmail){
            tfPhoneNumber.becomeFirstResponder()
        } else {
            tfPhoneNumber.resignFirstResponder()
        }
        return false
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        let viewController: RSSFormDetailViewController = segue.destinationViewController as! RSSFormDetailViewController
        viewController.theName = tfName.text
        viewController.theEmail = tfEmail.text
        viewController.thePhoneNumber = tfPhoneNumber.text
    }
}
