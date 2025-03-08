; ----------------------------------------
; Name : Radial Button by Filax
; Date : (C)2025 
; Site : https://github.com/BlackCreepyCat
; Description : A radial button with a cursor constrained to the contour, returning a value between ValMin and ValMax
; ----------------------------------------

; Structure pour le Bouton Radial
Type RadialButton
    Field X%          ; Position X absolue (coin sup�rieur gauche)
    Field Y%          ; Position Y absolue (coin sup�rieur gauche)
    Field Radius%     ; Rayon du bouton radial
    Field Angle#      ; Angle actuel du curseur (en degr�s)
    Field IsPressed%  ; �tat : est-ce que le bouton est press� ?
    Field ValMin#     ; Valeur minimale
    Field ValMax#     ; Valeur maximale
    Field Value#      ; Valeur actuelle (entre ValMin et ValMax)
End Type

; Fonction pour cr�er un bouton radial avec une valeur de d�part
Function CreateRadialButton.RadialButton(X%, Y%, Radius%, ValMin#, ValMax#, StartValue#)
    Local R.RadialButton = New RadialButton
    
    R\X% = X%
    R\Y% = Y%
    R\Radius% = Radius%
    R\IsPressed% = False
    R\ValMin# = ValMin#
    R\ValMax# = ValMax#
    
    ; V�rifier que StartValue est dans la plage ValMin � ValMax
    If StartValue# < ValMin# Then StartValue# = ValMin#
    If StartValue# > ValMax# Then StartValue# = ValMax#
    R\Value# = StartValue#
    
    ; Calculer l'angle initial � partir de StartValue
    R\Angle# = (R\Value# - R\ValMin#) / (R\ValMax# - R\ValMin#) * 360.0
    If R\Angle# < 0 Then R\Angle# = 0
    If R\Angle# > 360 Then R\Angle# = 360
    
    Return R
End Function

; Fonction pour mettre � jour le bouton radial
Function UpdateRadialButton(R.RadialButton)
    If R = Null Then Return
    
    Local CenterX# = R\X% + R\Radius%  ; Centre absolu X
    Local CenterY# = R\Y% + R\Radius%  ; Centre absolu Y
    
    ; V�rifier si la souris est dans la zone du bouton
    Local MouseInZone% = (MouseX() >= R\X% And MouseX() <= R\X% + R\Radius% * 2 And MouseY() >= R\Y% And MouseY() <= R\Y% + R\Radius% * 2)
    
    ; Gestion de l'�tat press�
    If MouseInZone% And MouseDown(1) Then
        R\IsPressed% = True
    ElseIf Not MouseDown(1) Then
        R\IsPressed% = False
		
    EndIf
    
    If R\IsPressed% Then
		
		DebugLog("WW "+R\Value#)
        ; Calculer l'angle par rapport au centre
        Local DeltaX# = MouseX() - CenterX#
        Local DeltaY# = MouseY() - CenterY#
		
		
        R\Angle# = ATan2(DeltaY#, DeltaX#)
        
        ; Calculer la valeur interpol�e entre ValMin et ValMax
        ; Normaliser l'angle (-180� � 180�) en 0� � 360� pour l'interpolation
        Local NormalizedAngle# = R\Angle#
        If NormalizedAngle# < 0 Then NormalizedAngle# = NormalizedAngle# + 360
        R\Value# = R\ValMin# + (R\ValMax# - R\ValMin#) * (NormalizedAngle# / 360.0)
    EndIf
End Function

; Fonction pour dessiner le bouton radial
Function DrawRadialButton(R.RadialButton)
    If R = Null Then Return
    
    Local CenterX# = R\X% + R\Radius%
    Local CenterY# = R\Y% + R\Radius%
    
    ; Dessiner le fond du bouton (cercle gris)
    Color 100, 100, 100
    Oval R\X%, R\Y%, R\Radius% * 2, R\Radius% * 2, 1
    
    ; Calculer la position du curseur sur le contour
    Local CursorX# = CenterX# + Cos(R\Angle#) * R\Radius%
    Local CursorY# = CenterY# + Sin(R\Angle#) * R\Radius%
    
    ; Dessiner le curseur (petit cercle)
    If R\IsPressed% Then
        Color 255, 100, 100  ; Rouge clair quand press�
    Else
        Color 200, 200, 200  ; Gris clair par d�faut
    EndIf
    Oval CursorX# - R\Radius% / 4, CursorY# - R\Radius% / 4, R\Radius% / 2, R\Radius% / 2, 1
    
    ; Dessiner les bordures
    Color 0, 0, 0
    Oval R\X%, R\Y%, R\Radius% * 2, R\Radius% * 2, 0
    
    ; Calculer l'angle pour la valeur 0
    Local ZeroAngle# = (0 - R\ValMin#) / (R\ValMax# - R\ValMin#) * 360.0
	
    If ZeroAngle# < 0 Then ZeroAngle# = 0
    If ZeroAngle# > 360 Then ZeroAngle# = 360
	
    Local ZeroX# = CenterX# + Cos(ZeroAngle#) * R\Radius%
    Local ZeroY# = CenterY# + Sin(ZeroAngle#) * R\Radius%
    
    ; Dessiner un petit cercle pour la valeur 0 (jaune)
    Color 255, 255, 0
    Oval ZeroX# - R\Radius% / 8, ZeroY# - R\Radius% / 8, R\Radius% / 4, R\Radius% / 4, 1
    
    ; Dessiner un petit cercle pour ValMin (bleu)
    Local MinAngle# = 0  ; ValMin est � 0� (droite)
    Local MinX# = CenterX# + Cos(MinAngle#) * R\Radius%
    Local MinY# = CenterY# + Sin(MinAngle#) * R\Radius%
    Color 0, 0, 255
    Oval MinX# - R\Radius% / 8, MinY# - R\Radius% / 8, R\Radius% / 4, R\Radius% / 4, 1
    
    ; Dessiner un petit cercle pour ValMax (vert)
    Local MaxAngle# = 180  ; ValMax est � 180� (gauche)
	
    Local MaxX# = CenterX# + Cos(MaxAngle#) * R\Radius%
    Local MaxY# = CenterY# + Sin(MaxAngle#) * R\Radius%
	
    Color 0, 255, 0
    Oval MaxX# - R\Radius% / 8, MaxY# - R\Radius% / 8, R\Radius% / 4, R\Radius% / 4, 1
	
	
	 ; Dessiner une ligne entre le centre et le curseur
    Color 255, 255, 255
    Line CenterX#, CenterY#, CursorX#, CursorY#
	
    
    ; Afficher la valeur actuelle au centre
    Color 255, 255, 255  ; Blanc pour le texte
    Text CenterX#, CenterY#, Str(Int(R\Value#)), True, True  ; Centr� au milieu du widget
End Function

; Exemple d'utilisation
Graphics3D 800, 600, 0, 2
SetBuffer BackBuffer()

; Cr�er deux boutons radiaux avec valeurs de d�part
Local RadialX.RadialButton = CreateRadialButton(30, 410, 70, -10, 10, 4)  ; Contr�le X, d�part � 4
Local RadialY.RadialButton = CreateRadialButton(600, 410, 70, -10, 10, -4) ; Contr�le Y, d�part � -4

; Cr�er la sc�ne 3D
Local Cube = CreateCube()
EntityColor Cube, 100, 100, 100
PositionEntity Cube, 0, 0, 0

Local Cam = CreateCamera()
CameraRange Cam, 0.01, 1000 
PositionEntity Cam, 0, 0, -3

Local Light = CreateLight()
PositionEntity Light, 0, 10, -10
PointEntity Light, Cube

Repeat
    Cls
    
    ; Mettre � jour les deux boutons radiaux
    UpdateRadialButton(RadialX)
    UpdateRadialButton(RadialY)
    
    ; Appliquer les valeurs � la rotation du cube (divis� par 5 pour une rotation douce)
    TurnEntity Cube, RadialX\Value# / 5, RadialY\Value# / 5, 0
    
    ; Rendu 3D
    RenderWorld
    
    ; Dessiner les boutons radiaux
    DrawRadialButton(RadialX)
    DrawRadialButton(RadialY)
    
    ; Afficher les informations de debug
    Color 255, 0, 0  ; Rouge pour RadialX
    Text 10, 10, "Angle X: " + Str(RadialX\Angle#) + "�"
    Text 10, 30, "Value X: " + Str(RadialX\Value#)
    
    Color 0, 255, 0  ; Vert pour RadialY
    Text 10, 50, "Angle Y: " + Str(RadialY\Angle#) + "�"
    Text 10, 70, "Value Y: " + Str(RadialY\Value#)
    
    Flip
Until KeyHit(1) ; Quitter avec �chap

; Nettoyage
FreeEntity Cube
FreeEntity Cam
FreeEntity Light
Delete RadialX
Delete RadialY
End
;~IDEal Editor Parameters:
;~C#Blitz3D