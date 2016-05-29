//
//  RSCustomAnnotation.swift
//  RSGuia Turistico
//
//  Created by Usuário Convidado on 12/03/16.
//  Copyright © 2016 Usuário Convidado. All rights reserved.
//

import UIKit
import MapKit

class RSCustomAnnotation: NSObject, MKAnnotation {
    
    var coordinate: CLLocationCoordinate2D
    var title: String?
    var subtitle: String?
    var ponto:RSPonto?
    
    init(coordinate: CLLocationCoordinate2D, title: String?,subtitle: String?, ponto:RSPonto?) {
        self.coordinate = coordinate
        self.title = title
        self.subtitle = subtitle
        self.ponto = ponto
    }
}
